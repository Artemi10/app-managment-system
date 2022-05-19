package com.devanmejia.appmanager.transfer.auth;

import com.devanmejia.appmanager.configuration.validator.EqualFields;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@EqualFields(
        baseField = "password",
        matchField = "rePassword",
        message = "Passwords do not match"
)
public record SignUpDTO(
        @NotBlank(message = "Email is incorrect")
        @Email(message = "Email is incorrect")
        String email,
        @NotBlank(message = "Password is incorrect")
        String password,
        @NotBlank(message = "Passwords do not match")
        String rePassword) {

}
