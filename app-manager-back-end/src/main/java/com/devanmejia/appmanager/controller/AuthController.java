package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/log-in")
    public Token logIn(@RequestBody @Valid LogInDTO logInDTO){
        return authService.logIn(logInDTO);
    }

    @PostMapping("/sign-up")
    public Token signUp(@RequestBody @Valid SignUpDTO signUpDTO){
        return authService.signUp(signUpDTO);
    }

    @PostMapping("/refresh")
    public Token refreshToken(@RequestBody Token token){
        return authService.refresh(token);
    }
}
