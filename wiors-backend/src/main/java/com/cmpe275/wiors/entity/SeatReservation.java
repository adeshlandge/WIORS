package com.cmpe275.wiors.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDate date;
    @ManyToOne
    @JsonIgnoreProperties({"address","employer","manager","reports"})
    private Employee employee;

    private Boolean isSelfReservation;
    
    @ManyToOne
    @JsonIgnoreProperties({"address","employer","manager","reports"})
    private Employee gtdReservedBy;
}
