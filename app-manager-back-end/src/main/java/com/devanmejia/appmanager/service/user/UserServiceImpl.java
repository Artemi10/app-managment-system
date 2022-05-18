package com.devanmejia.appmanager.service.user;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public String resetUser(String email) {
        var resetToken = RandomStringUtils.randomAlphabetic(8);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        user.setResetToken(resetToken);
        user.setAuthority(Authority.UPDATE_NOT_CONFIRMED);
        userRepository.save(user);
        return resetToken;
    }

    @Override
    public void confirmResetUser(String email, String resetToken) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        if (!user.getAuthority().equals(Authority.UPDATE_NOT_CONFIRMED)) {
            throw new BadCredentialsException("Confirmation is not allowed");
        }
        if (!user.getResetToken().equals(resetToken)) {
            throw new BadCredentialsException("Reset code is invalid");
        }
        user.setResetToken(null);
        user.setAuthority(Authority.UPDATE_CONFIRMED);
        userRepository.save(user);
    }

    @Override
    public void activateUser(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        user.setResetToken(null);
        user.setAuthority(Authority.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void updateUser(String email, UpdateDTO updateDTO) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        if (!user.getAuthority().equals(Authority.UPDATE_CONFIRMED)) {
            throw new BadCredentialsException("Update is not allowed");
        }
        var hashPassword = passwordEncoder.encode(updateDTO.newPassword());
        user.setPassword(hashPassword);
        user.setAuthority(Authority.ACTIVE);
        userRepository.save(user);
    }
}
