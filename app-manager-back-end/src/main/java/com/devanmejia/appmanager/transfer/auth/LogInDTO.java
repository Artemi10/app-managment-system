package com.devanmejia.appmanager.transfer.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record LogInDTO(
        @NotBlank(message = "Email is incorrect")
        @Email(message = "Email is incorrect")
        String email,
        @NotBlank(message = "Password is incorrect")
        String password) {
}
