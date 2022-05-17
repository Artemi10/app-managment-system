package com.devanmejia.appmanager.transfer.auth;

import com.devanmejia.appmanager.exception.ValidatorException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record SignUpDTO(
        @NotBlank(message = "Email is incorrect")
        @Email(message = "Email is incorrect")
        String email,
        @NotBlank(message = "Password is incorrect")
        String password,
        @NotBlank(message = "Passwords do not match")
        String rePassword) {

    public void validate() {
        if (!password.equals(rePassword)) {
            throw new ValidatorException("Passwords do not match");
        }
    }

}
