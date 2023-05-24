package com.cmpe275.wiors.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employer {

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Id
    @Column(updatable = false, nullable = false)
    private String id;
    @Column(nullable = false, unique = true)
    private String name;// required and must be unique
    private String description;
    private Address address;
    @OneToMany(mappedBy="employer")
    @JsonIgnore
    private Set<Employee> employees;
    private Integer capacity;
    private Integer mop = 0;
    //To setup authorization and authentication
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private UUID emailValidationToken;
    private Boolean isEmailValidated;

}
