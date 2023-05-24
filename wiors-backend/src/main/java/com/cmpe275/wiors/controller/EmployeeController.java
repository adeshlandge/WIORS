package com.cmpe275.wiors.controller;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.DashboardAnalyticsRequest;
import com.cmpe275.wiors.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PutMapping("/employer/{employerId}/employee/{employeeId}/updateMop/{mop}")
    public ResponseEntity<Employee> updateMop(@PathVariable String employerId,
                                              @PathVariable Long employeeId,
                                              @PathVariable Integer mop) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employeeService.updateMop(employerId,employeeId, mop),
                HttpStatus.OK);
    }

    @GetMapping("/employer/{employerId}/employee/{employeeId}/cancelMop")
    public ResponseEntity<Employee> updateMop(@PathVariable String employerId,
                                              @PathVariable Long employeeId) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employeeService.cancelMop(employerId,employeeId),
                HttpStatus.OK);
    }
    @PutMapping("/employer/{employerId}/employee/{employeeId}/updateGtd/{gtd}")
    public ResponseEntity<Employee> updateGtd(@PathVariable String employerId,
                                              @PathVariable Long employeeId,
                                              @PathVariable DayOfWeek gtd) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employeeService.specifyGtd(employerId, employeeId, gtd),
                HttpStatus.OK);
    }

    @DeleteMapping("/employer/{employerId}/employee/{employeeId}/cancelGtd")
    public ResponseEntity<Optional<Employee>> updateGtd(@PathVariable String employerId,
                                                       @PathVariable Long employeeId
                                                       ) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employeeService.cancelGtd(employerId, employeeId), HttpStatus.OK);
    }


    /***
     * Path:employer/{employerId}/employee/{id}?format={json | xml }
     * Method: GET
     * This returns the full form of the given employee entity with the given employer ID and employee
     * ID in the given format in its HTTP payload.
     * ● All existing fields, including the employer and list of collaborators should be returned. If
     * the employee of the given user ID does not exist, the HTTP return code should be 404;
     * 400 for other errors, or 200 if successful.
     * @param employerId
     * @param id
     * @param format
     * @return
     * @throws RecordDoesNotExistException
     */
    @GetMapping(path = "employer/{employerId}/employee/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Employee> getEmployee(
            @PathVariable(required = true) String employerId,
            @PathVariable(required = true) Long id,
            @RequestParam(required = false) String format
    ) throws RecordDoesNotExistException {
        return new ResponseEntity<>(employeeService.findEmployee(employerId, id, format),HttpStatus.OK);
    }

    @GetMapping(path = "employer/{employerId}/employee/{employeeId}/getAnalytics")
    public DashboardAnalyticsRequest getDashboardAnalytics(@PathVariable(required = true) String employerId,
                                                           @PathVariable(required = true) Long employeeId,
                                                           @RequestParam(required = true) String startDate,
                                                           @RequestParam(required = true) String endDate)
            throws RecordDoesNotExistException, BadRequestException {
        return employeeService.fetchAnalytics(startDate, endDate, employerId, employeeId);
    }
}
