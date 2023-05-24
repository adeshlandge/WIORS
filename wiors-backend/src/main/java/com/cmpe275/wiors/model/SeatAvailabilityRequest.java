package com.cmpe275.wiors.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
@Builder
@Getter
public class SeatAvailabilityRequest {
    private LocalDate startOfTheWeek;
    private String employerId;
    private Long employeeId;
}
