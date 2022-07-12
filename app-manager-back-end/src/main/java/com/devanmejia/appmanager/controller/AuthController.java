package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.service.auth.AuthService;
import com.devanmejia.appmanager.transfer.app.AppResponseDTO;
import com.devanmejia.appmanager.transfer.auth.LogInDTO;
import com.devanmejia.appmanager.transfer.auth.SignUpDTO;
import com.devanmejia.appmanager.transfer.auth.token.EnterToken;
import com.devanmejia.appmanager.transfer.auth.token.Token;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/log-in")
    @ApiOperation("Log in an existed user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 401, message = "Email and password combination is incorrect"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public Token logIn(@RequestBody @Valid LogInDTO logInDTO){
        return authService.logIn(logInDTO);
    }

    @PostMapping("/log-in/token")
    @ApiOperation("Log in an existed user via one-time entered token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 401, message = "Enter token is incorrect")
    })
    public Token logInViaEnterToken(@RequestBody EnterToken enterToken){
        return authService.logInViaEnterToken(enterToken);
    }

    @PostMapping("/sign-up")
    @ApiOperation("Sign up a new user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 401, message = "User has already been registered"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public Token signUp(@RequestBody @Valid SignUpDTO signUpDTO){
        return authService.signUp(signUpDTO);
    }

    @PostMapping("/refresh")
    @ApiOperation("Refresh access and refresh token combination")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = AppResponseDTO.class),
            @ApiResponse(code = 401, message = "Tokens combination is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid")
    })
    public Token refreshToken(@RequestBody Token token){
        return authService.refresh(token);
    }

}
