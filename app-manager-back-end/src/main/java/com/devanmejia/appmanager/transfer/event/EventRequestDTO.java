package com.devanmejia.appmanager.transfer.event;


import javax.validation.constraints.NotBlank;


public record EventRequestDTO(
        @NotBlank(message = "Event name is incorrect")
        String name,
        @NotBlank(message = "Add extra information")
        String extraInformation) {
}
