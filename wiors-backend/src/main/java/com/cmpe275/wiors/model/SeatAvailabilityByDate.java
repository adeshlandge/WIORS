package com.cmpe275.wiors.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@AllArgsConstructor
@Getter
@Setter
public class SeatAvailabilityByDate {
    private LocalDate date;
    //Is a seat available
    private Boolean isAvailable;
    //Can a seat be given via PreEmption
    private Boolean canBePreEmpted;
}
