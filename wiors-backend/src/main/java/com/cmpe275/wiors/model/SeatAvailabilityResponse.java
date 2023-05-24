package com.cmpe275.wiors.model;

import lombok.*;

import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatAvailabilityResponse {
    List<SeatAvailabilityByDate> seatAvailabilityByDateList;
    private Integer noOfDaysLeftToMeetMOP;
}
