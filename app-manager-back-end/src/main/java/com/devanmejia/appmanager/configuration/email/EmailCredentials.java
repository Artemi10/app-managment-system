package com.devanmejia.appmanager.configuration.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
@ConstructorBinding
@ConfigurationProperties("email.credentials")
public record EmailCredentials(
        @NotBlank(message = "Add email address")
        @Email(message = "Invalid email address")
        String address,
        @NotBlank(message = "Add email password")
        String password) {
}
