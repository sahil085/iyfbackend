package com.iyfbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDTO {

    private Long id;
    @NotNull(message = "Name cannot be null")
    private String name;
    private String email;
    @NotNull(message = "Contact cannot be null")
    private Long contact;
    @NotNull(message = "Location cannot be null")
    private String location;
    @NotNull(message = "Volunteer name cannot be null")
    private String registeredBy;
    @NotNull(message = "Bhagavad gita field should not be null")
    private Boolean withBhagavadGita;
    @NotNull(message = "Money paid field cannot be null")
    private Integer moneyPaid;
    private String remarks;
    private Integer moneyLeftToBePaid;
    private String registrationCode;

    private Boolean isPresent = false;


}
