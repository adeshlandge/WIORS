package com.cmpe275.wiors.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRegistrationRequest {
    @NotBlank
    @NotNull
    private String name;
    @Email
    @NotBlank
    @NotNull
    private String email;
    @NotBlank
    @NotNull
    private String password;
    @NotNull
    @NotBlank
    private String employerId;

    private String managerEmailId;

    private String title;
    private String street;
    private String city;
    private String state;
    private String zip;


}
