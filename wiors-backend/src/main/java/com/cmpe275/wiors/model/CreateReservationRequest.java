package com.cmpe275.wiors.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {

    private String emailId;
    private LocalDate startDate;
    private LocalDate endDate;

}
