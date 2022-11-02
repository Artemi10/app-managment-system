package com.devanmejia.appmanager.service.user;

import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.exception.EntityException;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.service.token.TokenGenerator;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final static int RESET_TOKEN_LENGTH = 4;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenGenerator numericTokenGenerator;

    @Autowired
    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            @Qualifier("numericTokenGenerator")
            TokenGenerator numericTokenGenerator
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.numericTokenGenerator = numericTokenGenerator;
    }

    @Override
    @Transactional
    public String resetUser(String email) {
        var resetToken = numericTokenGenerator.generatorToken(RESET_TOKEN_LENGTH);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        user.setResetToken(passwordEncoder.encode(resetToken));
        user.setAuthority(Authority.UPDATE_NOT_CONFIRMED);
        userRepository.save(user);
        return resetToken;
    }

    @Override
    @Transactional
    public void confirmResetUser(String email, String resetToken) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        if (!user.getAuthority().equals(Authority.UPDATE_NOT_CONFIRMED)) {
            throw new BadCredentialsException("Confirmation is not allowed");
        }
        var isTokenValid = user.getResetToken()
                .map(token -> passwordEncoder.matches(resetToken, token))
                .orElse(false);
        if (!isTokenValid) {
            throw new BadCredentialsException("Reset code is invalid");
        }
        user.setResetToken(null);
        user.setAuthority(Authority.UPDATE_CONFIRMED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityException("User not found"));
        user.setResetToken(null);
        user.setAuthority(Authority.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
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
