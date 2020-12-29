package com.iyfbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDTO {

    @NotNull(message = "Name cannot be null")
    private String name;
    private String email;
    @NotNull(message = "Contact cannot be null")
    private Long contact;
    @NotNull(message = "Location cannot be null")
    private String location;
    @NotNull(message = "Volunteer name cannot be null")
    private String registeredBy;

}
