package com.devanmejia.appmanager.repository;

import com.devanmejia.appmanager.entity.user.Authority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    public void findByEmail_When_User_Is_Active_Test(){
        var actual = Assertions.assertDoesNotThrow(() -> userRepository.findByEmail("lyah.artem10@mail.ru"));
        assertTrue(actual.isPresent());
        var user = actual.get();
        assertEquals(1, user.getId());
        assertEquals("lyah.artem10@mail.ru", user.getEmail());
        assertEquals("$2y$10$ZPgg5k.SQaJIxjGF7AU15.GNVF2U7MVJJWgMxkyuXjW550XIEEK52", user.getPassword());
        assertEquals(Authority.ACTIVE, user.getAuthority());
        assertTrue(user.getResetToken().isEmpty());
    }

    @Test
    public void findByEmail_When_User_Has_Update_Confirmed_Authority_Test(){
        var actual = Assertions.assertDoesNotThrow(() -> userRepository.findByEmail("d10@gmail.com"));
        assertTrue(actual.isPresent());
        var user = actual.get();
        assertEquals(3, user.getId());
        assertEquals("d10@gmail.com", user.getEmail());
        assertEquals("$2y$10$x.jaNOvtBnsMqyhehZ5ituZzUAGnrHiSXzme1/i0EzrcWgRHMl0Ve", user.getPassword());
        assertEquals(Authority.UPDATE_CONFIRMED, user.getAuthority());
        assertEquals("f49a1320-4e9b-4a81-b5ee-835c59f35fed", user.getRefreshToken());
    }

    @Test
    public void findByEmail_When_User_Has_Update_Not_Confirmed_Authority_Test(){
        var actual = Assertions.assertDoesNotThrow(() -> userRepository.findByEmail("d11@gmail.com"));
        assertTrue(actual.isPresent());
        var user = actual.get();
        assertEquals(4, user.getId());
        assertEquals("d11@gmail.com", user.getEmail());
        assertEquals("$2a$10$ypeGI68R5lQHc..n1TymQe1Xn6paLc.CPafoEM.u5R0q/qhuLc8Zq", user.getPassword());
        assertEquals(Authority.UPDATE_NOT_CONFIRMED, user.getAuthority());
        var resetToken = user.getResetToken();
        assertTrue(resetToken.isPresent());
        assertEquals("DVhclnWO", resetToken.get());
        assertEquals("f49a1320-4e9b-4a81-b5ee-835c59f35fed", user.getRefreshToken());
    }

    @Test
    public void findByEmail_When_User_Does_Not_Exist_Test(){
        var actual = userRepository.findByEmail("lyakh@mail.ru");
        assertTrue(actual.isEmpty());
    }
}
