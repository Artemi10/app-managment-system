package com.devanmejia.appmanager.controller;

import com.devanmejia.appmanager.configuration.security.details.UserPrincipal;
import com.devanmejia.appmanager.configuration.security.token.AccessTokenService;
import com.devanmejia.appmanager.entity.user.Authority;
import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.service.email.MessageService;
import com.devanmejia.appmanager.service.user.UserService;
import com.devanmejia.appmanager.transfer.auth.UpdateDTO;
import com.devanmejia.appmanager.transfer.auth.token.AccessToken;
import com.devanmejia.appmanager.transfer.auth.token.ResetToken;
import com.devanmejia.appmanager.transfer.email.EmailRequestDTO;
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
    public void updateUser(
            @RequestBody @Valid UpdateDTO requestBody,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        var email = userPrincipal.email();
        userService.updateUser(email, requestBody);
    }

    @PostMapping("/reset")
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
    public void resetUserAgain(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var email = userPrincipal.email();
        var resetToken = userService.resetUser(email);
        messageService.sendMessage(email, resetToken);
    }

    @PostMapping("/reset/confirm")
    public AccessToken confirmResetUser(
            @RequestBody ResetToken token,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var email = principal.email();
        userService.confirmResetUser(email, token.resetToken());
        var accessToken = accessTokenService
                .createAccessToken(email, Authority.UPDATE_CONFIRMED);
        return new AccessToken(accessToken);
    }
}
