package com.cmpe275.wiors.controller;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.SeatReservation;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.*;
import com.cmpe275.wiors.service.EmployeeService;
import com.cmpe275.wiors.service.ReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/employer/{employerId}/employee/{employeeId}/availability/{startOfTheWeek}")
    public SeatAvailabilityResponse getSeatAvailability(@PathVariable(required = true) String employerId,
                                                        @PathVariable(required = true) Long employeeId,
                                                        @PathVariable(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfTheWeek){
        return reservationService.getSeatAvailability(SeatAvailabilityRequest.builder()
                                                .employeeId(employeeId)
                                                .employerId(employerId)
                                                .startOfTheWeek(startOfTheWeek)
                                                .build());
    }
    @PostMapping("/employer/{employerId}/employee/{employeeId}")
    public ResponseEntity<?> createReservation(
            @PathVariable String employerId,
            @PathVariable Long employeeId,
            @RequestBody @Valid CreateReservationRequest request) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(reservationService.createSeatReservation(request, true, null),
                HttpStatus.OK);


    }

    @GetMapping("/employer/{employerId}/employee/{employeeId}/all")
    public ResponseEntity<?> getReservations(
            @PathVariable String employerId,
            @PathVariable Long employeeId) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(reservationService.getEmployeeAllReservations(employerId, employeeId),
                HttpStatus.OK);
    }

    @DeleteMapping("/employer/{employerId}/employee/{employeeId}/all")
    public ResponseEntity<?> deleteReservations(
            @PathVariable String employerId,
            @PathVariable Long employeeId) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(reservationService.deleteEmployeeReservations(employerId, employeeId),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReservationById(
            @PathVariable Long id) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(reservationService.deleteReservationById(id),
                HttpStatus.OK);
    }

    @DeleteMapping("/employer/{employerId}/all")
    public ResponseEntity<?> deleteReservations(
            @PathVariable String employerId) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(reservationService.deleteAllEmployerReservations(employerId),
                HttpStatus.OK);
    }

    @GetMapping("/employer/{employerId}/employee/{employeeId}/compliance")
    public ResponseEntity<?> getComplianceTable(@PathVariable(required = true) String employerId,
                                                  @PathVariable(required = true) Long employeeId) throws RecordDoesNotExistException {
        LocalDate currentDate = LocalDate.now();

        Employee employee = employeeService.findEmployee(employerId, employeeId, null);
        List<SeatReservation> seatReservations =
        reservationService.getEmployeeAllReservations(employerId, employeeId);

        Map<LocalDate, List<SeatReservation>> reservationsByWeek = seatReservations.stream()
                .collect(Collectors.groupingBy(ReservationController::getStartOfWeek));

        List<ComplianceRow> complianceTable = reservationsByWeek.entrySet().stream()
                .map(entry -> {
                    LocalDate weekStartDate = entry.getKey();
                    List<SeatReservation> reservations = entry.getValue();

                    ComplianceRow complianceRow = new ComplianceRow();
                    complianceRow.setWeekStartDate(weekStartDate);

                    List<LocalDate> gtdDates = new ArrayList<>();
                    List<LocalDate> selfReservationDates = new ArrayList<>();

                    reservations.forEach(reservation -> {
                        if (reservation.getIsSelfReservation()) {
                            selfReservationDates.add(reservation.getDate());
                        } else {
                            gtdDates.add(reservation.getDate());
                        }
                    });

                    int allReservationsCount = reservations.size();
                    boolean mopMet = allReservationsCount >= employee.getMop();
                    boolean preemptable = allReservationsCount > employee.getMop();

                    complianceRow.setGtdDates(gtdDates);
                    complianceRow.setMopMet(mopMet);
                    complianceRow.setPreemptable(preemptable);
                    complianceRow.setSelfReservationDates(selfReservationDates);


                    return complianceRow;
                })
                .sorted(Comparator.comparing(ComplianceRow::getWeekStartDate))
                .collect(Collectors.toList());

        return new ResponseEntity<>(complianceTable,
                HttpStatus.OK);
    }

    private static LocalDate getStartOfWeek(SeatReservation reservation) {
        return reservation.getDate().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }
}
