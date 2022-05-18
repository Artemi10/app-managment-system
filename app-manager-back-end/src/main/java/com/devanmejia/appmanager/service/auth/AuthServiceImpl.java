package com.devanmejia.appmanager.service.auth;

import com.devanmejia.appmanager.configuration.security.token.AccessTokenService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.AccessToken;
import com.devanmejia.appmanager.transfer.auth.token.ResetToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccessTokenService accessTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Token logIn(LogInDTO logInDTO) {
        var user = userRepository
                .findByEmail(logInDTO.email())
                .orElseThrow(() -> new BadCredentialsException("Email and password combination is incorrect"));
        var isPasswordMatches = passwordEncoder
                .matches(logInDTO.password(), user.getPassword());
        if (!isPasswordMatches) {
            throw new BadCredentialsException("Email and password combination is incorrect");
        }
        var refreshToken = UUID.randomUUID().toString();
        user.setAuthority(Authority.ACTIVE);
        user.setResetToken(null);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        var accessToken = accessTokenService
                .createAccessToken(user.getEmail(), user.getAuthority());
        return new Token(accessToken, refreshToken);
    }

    @Override
    public Token signUp(SignUpDTO signUpDTO) {
        var userOptional = userRepository.findByEmail(signUpDTO.email());
        if (userOptional.isPresent()) {
            throw new BadCredentialsException("User has already been registered");
        }
        var hashPassword = passwordEncoder.encode(signUpDTO.password());
        var refreshToken = UUID.randomUUID().toString();
        var newUser = User.builder()
                .email(signUpDTO.email())
                .password(hashPassword)
                .refreshToken(refreshToken)
                .authority(Authority.ACTIVE)
                .apps(new ArrayList<>())
                .build();
        userRepository.save(newUser);
        var accessToken = accessTokenService
                .createAccessToken(newUser.getEmail(), newUser.getAuthority());
        return new Token(accessToken, refreshToken);
    }

    @Override
    public Token refresh(Token token) {
        var email = accessTokenService.getEmail(token.accessToken());
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Tokens combination is invalid"));
        var isResetCodeMatches = user.getRefreshToken().equals(token.refreshToken());
        if (!isResetCodeMatches) {
            throw new BadCredentialsException("Tokens combination is invalid");
        }
        var accessToken = accessTokenService.createAccessToken(user.getEmail(), user.getAuthority());
        var refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return new Token(accessToken, refreshToken);
    }
}
