package com.devanmejia.appmanager.service.auth;

import com.devanmejia.appmanager.security.token.AccessTokenService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.entity.user.User;
import com.devanmejia.appmanager.repository.UserRepository;
import com.devanmejia.appmanager.service.token.TokenGenerator;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.EnterToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AuthServiceImpl implements AuthService {
    private final static int REFRESH_TOKEN_LENGTH = 64;
    private final static int ENTER_TOKEN_LENGTH = 64;
    private final static int DEFAULT_PASSWORD_LENGTH = 10;

    private final AccessTokenService accessTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenGenerator secureTokenGenerator;

    @Autowired
    public AuthServiceImpl(
            AccessTokenService accessTokenService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            @Qualifier("secureTokenGenerator")
            TokenGenerator secureTokenGenerator
    ) {
        this.accessTokenService = accessTokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.secureTokenGenerator = secureTokenGenerator;
    }

    @Override
    @Transactional
    public Token logIn(LogInDTO logInDTO) {
        var user = userRepository
                .findByEmail(logInDTO.email())
                .orElseThrow(() -> new BadCredentialsException("Email and password combination is incorrect"));
        var isPasswordMatches = passwordEncoder
                .matches(logInDTO.password(), user.getPassword());
        if (!isPasswordMatches) {
            throw new BadCredentialsException("Email and password combination is incorrect");
        }
        var refreshToken = secureTokenGenerator.generatorToken(REFRESH_TOKEN_LENGTH);
        user.setAuthority(Authority.ACTIVE);
        user.setResetToken(null);
        user.setRefreshToken(passwordEncoder.encode(refreshToken));
        userRepository.save(user);
        var accessToken = accessTokenService
                .createAccessToken(user.getEmail(), user.getAuthority());
        return new Token(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public Token logInViaEnterToken(EnterToken enterToken) {
        var user = userRepository
                .findUserByOauthEnterToken(enterToken.enterToken())
                .orElseThrow(() -> new BadCredentialsException("Enter token is incorrect"));
        user.setOauthEnterToken(null);
        userRepository.save(user);
        var accessToken = accessTokenService
                .createAccessToken(user.getEmail(), user.getAuthority());
        return new Token(accessToken, user.getRefreshToken());
    }

    @Override
    @Transactional
    public Token signUp(SignUpDTO signUpDTO) {
        var userOptional = userRepository.findByEmail(signUpDTO.email());
        if (userOptional.isPresent()) {
            throw new BadCredentialsException("User has already been registered");
        }
        var newUser = createNewUser(signUpDTO.email(), signUpDTO.password());
        var accessToken = accessTokenService
                .createAccessToken(newUser.getEmail(), newUser.getAuthority());
        return new Token(accessToken, newUser.getRefreshToken());
    }

    @Override
    @Transactional
    public String logInWithOAuth(String email) {
        var userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isEmpty()) {
            var defaultPassword= secureTokenGenerator.generatorToken(DEFAULT_PASSWORD_LENGTH);
            user = createNewUser(email, defaultPassword);
        }
        else {
            user = userOptional.get();
        }
        var enterToken = secureTokenGenerator.generatorToken(ENTER_TOKEN_LENGTH);
        user.setOauthEnterToken(enterToken);
        userRepository.save(user);
        return enterToken;
    }

    private User createNewUser(String email, String password) {
        var hashPassword = passwordEncoder.encode(password);
        var refreshToken = secureTokenGenerator.generatorToken(REFRESH_TOKEN_LENGTH);
        var newUser = User.builder()
                .email(email)
                .password(hashPassword)
                .refreshToken(passwordEncoder.encode(refreshToken))
                .authority(Authority.ACTIVE)
                .apps(new ArrayList<>())
                .build();
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public Token refresh(Token token) {
        var email = accessTokenService.getEmail(token.accessToken());
        var user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Tokens combination is invalid"));
        var isRefreshCodeMatches = passwordEncoder.matches(token.refreshToken(), user.getRefreshToken());
        if (!isRefreshCodeMatches) {
            throw new BadCredentialsException("Tokens combination is invalid");
        }
        var accessToken = accessTokenService.createAccessToken(user.getEmail(), user.getAuthority());
        var refreshToken = secureTokenGenerator.generatorToken(REFRESH_TOKEN_LENGTH);
        user.setRefreshToken(passwordEncoder.encode(refreshToken));
        userRepository.save(user);
        return new Token(accessToken, refreshToken);
    }
}
