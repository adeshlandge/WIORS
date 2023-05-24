package com.cmpe275.wiors.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployerRegistrationRequest {
    @NotBlank
    @NotNull
    private String name;// required and must be unique
    @Email
    @NotBlank
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String password;
    private String description;
    private String street; // e.g., 100 Main ST
    private String city;
    private String state;
    private String zip;
    @NotNull
    private Integer capacity;
}
