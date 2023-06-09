package com.cmpe275.wiors.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAnalyticsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private long overAllAttendanceMeetRate;
    private long employeeComplianceRate;
    private long additionalSeatsRequired;
}
