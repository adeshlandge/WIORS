package com.cmpe275.wiors.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {

    @EmbeddedId
    @JsonUnwrapped
    private EmployeeId employeeId;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    private String title;
    private Address address;

    @ManyToOne
    @JoinColumn(name="employerId", nullable=false, updatable = false, insertable = false, referencedColumnName = "id")
    @JsonIgnoreProperties({"address","employees"})
    private Employer employer;

    @ManyToOne
    @JsonIgnoreProperties({"address","employer","manager","reports"})
    private Employee manager;

    @OneToMany(mappedBy="manager")
    @JsonIgnoreProperties({"address","employer","manager","reports"})
    private List<Employee> reports; // director reports who have the current employee as their manager.

    private Integer mop = 0;
    private DayOfWeek gtd;
    //To setup authorization and authentciation
    @JsonIgnore
    private String password;
    //Seat Reservation for employee
    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<SeatReservation> seatReservationsList;
    @JsonIgnore
    private UUID emailValidationToken;
    private Boolean isEmailValidated;

}