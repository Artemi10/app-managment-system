package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.security.details.UserPrincipal;
import com.devanmejia.appmanager.security.token.AccessTokenService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.service.email.MessageService;
import com.devanmejia.appmanager.service.user.UserService;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import com.devanmejia.appmanager.transfer.auth.token.AccessToken;
import com.devanmejia.appmanager.transfer.auth.token.ResetToken;
import com.devanmejia.appmanager.transfer.email.EmailRequestDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final MessageService messageService;
    private final AccessTokenService accessTokenService;
    private final UserService userService;

    @PatchMapping
    @ApiOperation("Update user password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Update is not allowed"),
            @ApiResponse(code = 422, message = "Request body is invalid"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public void updateUser(
            @RequestBody @Valid UpdateDTO requestBody,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        var email = userPrincipal.email();
        userService.updateUser(email, requestBody);
    }

    @PostMapping("/reset")
    @ApiOperation("Reset user and send password recovery email")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 422, message = "Request body is invalid"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public AccessToken resetUser(
            @RequestBody @Valid EmailRequestDTO requestBody) {
        var email = requestBody.email();
        try {
            var resetToken = userService.resetUser(email);
            messageService.sendMessage(email, resetToken);
            var accessToken = accessTokenService
                    .createAccessToken(email, Authority.UPDATE_NOT_CONFIRMED);
            return new AccessToken(accessToken);
        } catch (EmailException exception) {
            userService.activateUser(email);
            throw exception;
        }
    }

    @PostMapping("/reset/again")
    @ApiOperation("Reset user and send password recovery email again")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Access token is invalid"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public void resetUserAgain(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var email = userPrincipal.email();
        var resetToken = userService.resetUser(email);
        messageService.sendMessage(email, resetToken);
    }

    @PostMapping("/reset/confirm")
    @ApiOperation("Confirm email via reset token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "User is not authorized"),
            @ApiResponse(code = 403, message = "Confirmation is not allowed"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public AccessToken confirmResetUser(
            @RequestBody ResetToken token,
            @AuthenticationPrincipal UserPrincipal principal) {
        var email = principal.email();
        userService.confirmResetUser(email, token.resetToken());
        var accessToken = accessTokenService
                .createAccessToken(email, Authority.UPDATE_CONFIRMED);
        return new AccessToken(accessToken);
    }
}
