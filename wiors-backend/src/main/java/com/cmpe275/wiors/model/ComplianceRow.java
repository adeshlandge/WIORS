package com.cmpe275.wiors.model;

import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceRow {
    private LocalDate weekStartDate;
    private List<LocalDate> gtdDates;
    private List<LocalDate> selfReservationDates;
    private boolean mopMet;
    private boolean preemptable;
}
