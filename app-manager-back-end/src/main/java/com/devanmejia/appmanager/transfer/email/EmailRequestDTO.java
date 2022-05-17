package com.devanmejia.appmanager.transfer.email;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record EmailRequestDTO(
        @NotBlank(message = "Email is incorrect")
        @Email(message = "Email is incorrect")
        String email) {
}
