package com.devanmejia.appmanager.transfer.app;



import javax.validation.constraints.NotBlank;

public record AppRequestDTO(
        @NotBlank(message = "Application name is incorrect")
        String name) {
}
