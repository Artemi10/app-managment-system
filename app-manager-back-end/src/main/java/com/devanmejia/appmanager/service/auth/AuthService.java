package com.devanmejia.appmanager.service.auth;

import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.AccessToken;
import com.devanmejia.appmanager.transfer.auth.token.EnterToken;
import com.devanmejia.appmanager.transfer.auth.token.ResetToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    Token logIn(LogInDTO logInDTO);
    Token logInViaEnterToken(EnterToken enterToken);
    Token signUp(SignUpDTO signUpDTO);
    Token refresh(Token token);
    String logInWithOAuth(String email);
}
