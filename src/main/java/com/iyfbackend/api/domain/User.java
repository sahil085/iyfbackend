package com.iyfbackend.api.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    private String email;
    @NotNull
    @Column(unique = true)
    private Long contact;
    @NotNull
    private String location;
    @NotNull
    private String registeredBy;
    @NotNull
    private LocalDateTime registeredOn;
    @NotNull
    private String registrationCode;
    @NotNull
    @Column(columnDefinition = "bit default false")
    private Boolean withBhagavadGita = false;
    @NotNull
    private Integer moneyPaid;
    private String remarks;
    private String institute;

}
