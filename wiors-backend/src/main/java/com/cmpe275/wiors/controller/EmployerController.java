package com.cmpe275.wiors.controller;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.Employer;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.EmployeeRegistrationRequest;
import com.cmpe275.wiors.service.EmployeeService;
import com.cmpe275.wiors.service.EmployerService;
import com.cmpe275.wiors.util.CrossEntityValidations;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@NoArgsConstructor
public class EmployerController {
    @Autowired
    EmployerService employerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CrossEntityValidations crossEntityValidations;

    @GetMapping(path = "employer/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Employer> getEmployee(
            @PathVariable(required = true) String id
    ) throws RecordDoesNotExistException {
        return new ResponseEntity<>(employerService.findEmployer(id), HttpStatus.OK);
    }
    @PutMapping("/employer/{employerId}/updateMop/{mop}")
    public ResponseEntity<Employer> updateMop(@PathVariable String employerId,
                                              @PathVariable Integer mop) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employerService.updateMop(employerId, mop),
                HttpStatus.OK);
    }

    @GetMapping("/employer/{employerId}/cancelMop")
    public ResponseEntity<Employer> cancelMop(@PathVariable String employerId) throws RecordDoesNotExistException, BadRequestException {

        return new ResponseEntity<>(employerService.cancelMop(employerId),
                HttpStatus.OK);
    }
    @PostMapping("/employer/{employerId}/registeremployees/employees/upload")
    public ResponseEntity<String> handleBulkEmployeeRegistration(@PathVariable String employerId,
                                              @RequestParam("file") MultipartFile file) throws RecordDoesNotExistException, BadRequestException, IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file. Please provide a valid file");
        }
        employerService.handleBulkEmployeeRegistration(employerId, file);
        return new ResponseEntity<>("All the employees are registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/employer/{employerId}/reserveseats/employees/upload")
    public ResponseEntity<String> handleBulkSeatReservation(@PathVariable String employerId,
                                                   @RequestParam("file") MultipartFile file) throws RecordDoesNotExistException, BadRequestException, IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file. Please provide a valid file");
        }
        employerService.handleBulkSeatReservation(employerId, file);
        return new ResponseEntity<>("Seats have been reserved for all the employees successfully!", HttpStatus.OK);
    }

    @PutMapping("/employer/{employerId}/updateCapacity/{capacity}")
    public ResponseEntity<Employer> updateCapacity(@PathVariable String employerId,
                                              @PathVariable Integer capacity) throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employerService.updateCapacity(employerId,  capacity),
                HttpStatus.OK);
    }

    @GetMapping("/employer/all")
    public ResponseEntity<List<Employer>> getEmployers() throws RecordDoesNotExistException, BadRequestException {
        return new ResponseEntity<>(employerService.getAllEmployers(),
                HttpStatus.OK);
    }

}
