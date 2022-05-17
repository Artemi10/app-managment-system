package com.devanmejia.appmanager.transfer.auth;

import com.devanmejia.appmanager.exception.ValidatorException;

import javax.validation.constraints.NotBlank;

public record UpdateDTO(
        @NotBlank(message = "Password is incorrect")
        String newPassword,
        @NotBlank(message = "Passwords do not match")
        String rePassword) {

    public void validate() {
        if (!newPassword.equals(rePassword)) {
            throw new ValidatorException("Passwords do not match");
        }
    }

}
