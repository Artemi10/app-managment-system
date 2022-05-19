package com.devanmejia.appmanager.transfer.auth;

import com.devanmejia.appmanager.configuration.validator.EqualFields;

import javax.validation.constraints.NotBlank;

@EqualFields(
        baseField = "newPassword",
        matchField = "rePassword",
        message = "Passwords do not match"
)
public record UpdateDTO(
        @NotBlank(message = "Password is incorrect")
        String newPassword,
        @NotBlank(message = "Passwords do not match")
        String rePassword) {
}
