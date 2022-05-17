package com.devanmejia.appmanager.service.user;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplTest() {
        this.userRepository = spy(UserRepository.class);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = new UserServiceImpl(passwordEncoder, userRepository);
    }

    @BeforeEach
    public void initMock() {
        var activeUser = User.builder()
                .id(1)
                .email("lyah.artem10@mail.ru")
                .password(passwordEncoder.encode("2424285"))
                .authority(Authority.ACTIVE)
                .resetToken("")
                .apps(new ArrayList<>())
                .build();
        var updateConfirmedUser = User.builder()
                .id(2)
                .email("lyah.artem10@gmail.com")
                .password(passwordEncoder.encode("qwerty"))
                .authority(Authority.UPDATE_CONFIRMED)
                .apps(new ArrayList<>())
                .build();
        var updateNotConfirmedUser = User.builder()
                .id(3)
                .email("lyah.artem11@gmail.com")
                .password(passwordEncoder.encode("qwerty"))
                .authority(Authority.UPDATE_NOT_CONFIRMED)
                .resetToken("DVhclnWO")
                .apps(new ArrayList<>())
                .build();
        when(userRepository.findByEmail("lyah.artem10@mail.ru"))
                .thenReturn(Optional.of(activeUser));
        when(userRepository.findByEmail("lyah.artem10@gmail.com"))
                .thenReturn(Optional.of(updateConfirmedUser));
        when(userRepository.findByEmail("lyah.artem11@gmail.com"))
                .thenReturn(Optional.of(updateNotConfirmedUser));
        when(userRepository.findByEmail(
                argThat(email -> !email.equals("lyah.artem10@gmail.com")
                        && !email.equals("lyah.artem10@mail.ru")
                        && !email.equals("lyah.artem11@gmail.com"))))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(answer -> {
                    var user = (User) answer.getArgument(0);
                    return User.builder()
                            .id(3)
                            .email(user.getEmail())
                            .password(user.getPassword())
                            .authority(user.getAuthority())
                            .resetToken(user.getResetToken())
                            .apps(user.getApps())
                            .build();
                });
    }

    @Test
    public void reset_Existent_Active_User() {
        assertDoesNotThrow(() -> userService
                .resetUser("lyah.artem10@mail.ru"));
        verify(userRepository, times(1)).findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.UPDATE_NOT_CONFIRMED)
                                && userToReset.getEmail().equals("lyah.artem10@mail.ru")
                                && passwordEncoder.matches("2424285", userToReset.getPassword())
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void reset_Existent_Update_Confirmed_User() {
        assertDoesNotThrow(() -> userService
                .resetUser("lyah.artem10@gmail.com"));
        verify(userRepository, times(1)).findByEmail("lyah.artem10@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.UPDATE_NOT_CONFIRMED)
                                && userToReset.getEmail().equals("lyah.artem10@gmail.com")
                                && passwordEncoder.matches("qwerty", userToReset.getPassword())
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void reset_Existent_Update_Not_Confirmed_User() {
        assertDoesNotThrow(() -> userService
                .resetUser("lyah.artem11@gmail.com"));
        verify(userRepository, times(1)).findByEmail("lyah.artem11@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.UPDATE_NOT_CONFIRMED)
                                && userToReset.getEmail().equals("lyah.artem11@gmail.com")
                                && passwordEncoder.matches("qwerty", userToReset.getPassword())
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void throw_Exception_When_Reset_Nonexistent_User() {
        var exception = assertThrows(EntityException.class, () -> userService
                .resetUser("lyah.artem03@gmail.com"));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("lyah.artem03@gmail.com");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void activate_Existent_Active_User() {
        assertDoesNotThrow(() -> userService.activateUser("lyah.artem10@mail.ru"));
        verify(userRepository, times(1)).findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.ACTIVE)
                                && userToReset.getEmail().equals("lyah.artem10@mail.ru")
                                && passwordEncoder.matches("2424285", userToReset.getPassword())
                                && userToReset.getResetToken() == null
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void activate_Existent_Update_Confirmed_User() {
        assertDoesNotThrow(() -> userService.activateUser("lyah.artem10@gmail.com"));
        verify(userRepository, times(1)).findByEmail("lyah.artem10@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.ACTIVE)
                                && userToReset.getEmail().equals("lyah.artem10@gmail.com")
                                && passwordEncoder.matches("qwerty", userToReset.getPassword())
                                && userToReset.getResetToken() == null
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void activate_Existent_Update_Not_Confirmed_User() {
        assertDoesNotThrow(() -> userService.activateUser("lyah.artem11@gmail.com"));
        verify(userRepository, times(1)).findByEmail("lyah.artem11@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(
                        userToReset -> userToReset.getAuthority().equals(Authority.ACTIVE)
                                && userToReset.getEmail().equals("lyah.artem11@gmail.com")
                                && passwordEncoder.matches("qwerty", userToReset.getPassword())
                                && userToReset.getResetToken() == null
                                && userToReset.getApps().isEmpty())
                );
    }

    @Test
    public void throw_Exception_When_Activate_Nonexistent_User() {
        var exception = assertThrows(EntityException.class, () -> userService
                .activateUser("lyah.artem03@gmail.com"));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("lyah.artem03@gmail.com");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void update_Reset_User_When_Tokens_Match() {
        var validUpdateDTO = new UpdateDTO("2424285", "2424285");
        assertDoesNotThrow(() -> userService.updateUser("lyah.artem10@gmail.com", validUpdateDTO));
        verify(userRepository, times(1)).findByEmail("lyah.artem10@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(
                        user -> user.getResetToken() == null
                                && user.getAuthority().equals(Authority.ACTIVE)
                                && passwordEncoder.matches(validUpdateDTO.newPassword(), user.getPassword())
                                && user.getEmail().equals("lyah.artem10@gmail.com")
                                && user.getApps().isEmpty()
                ));
    }

    @Test
    public void throw_Exception_When_Update_Active_User() {
        var updateDTO = new UpdateDTO("qwerty", "qwerty");
        var exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.updateUser("lyah.artem10@mail.ru", updateDTO));
        assertEquals("Update is not allowed", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void throw_Exception_When_Update_Nonexistent_User() {
        var updateDTO = new UpdateDTO("qwerty", "qwerty");
        var exception = assertThrows(
                EntityException.class,
                () -> userService.updateUser("lyah.artem03@mail.ru", updateDTO));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("lyah.artem03@mail.ru");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    public void confirmResetUser_If_User_Has_Update_Not_Confirmed_Authority() {
        assertDoesNotThrow(() -> userService
                .confirmResetUser("lyah.artem11@gmail.com", "DVhclnWO"));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem11@gmail.com");
        verify(userRepository, times(1))
                .save(argThat(user -> user.getResetToken() == null
                        && user.getAuthority().equals(Authority.UPDATE_CONFIRMED)));
    }

    @Test
    public void throw_Exception_When_Reset_Token_Is_Invalid() {
        assertThrows(BadCredentialsException.class,
                () -> userService.confirmResetUser("lyah.artem10@mail.ru", "kVhclnWO"));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void throw_Exception_When_confirmResetUser_If_User_Has_Update_Confirmed_Authority() {
        assertThrows(BadCredentialsException.class,
                () -> userService.confirmResetUser("lyah.artem10@gmail.com", "DVhclnWO"));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@gmail.com");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void throw_Exception_When_confirmResetUser_If_User_Has_Active_Authority() {
        assertThrows(BadCredentialsException.class,
                () -> userService.confirmResetUser("lyah.artem10@mail.ru", "DVhclnWO"));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.ru");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void throw_Exception_When_confirmResetUser_If_User_Does_Not_Exist_Authority() {
        assertThrows(EntityException.class,
                () -> userService.confirmResetUser("lyah.artem10@mail.com", "DVhclnWO"));
        verify(userRepository, times(1))
                .findByEmail("lyah.artem10@mail.com");
        verify(userRepository, times(0)).save(any(User.class));
    }
}
