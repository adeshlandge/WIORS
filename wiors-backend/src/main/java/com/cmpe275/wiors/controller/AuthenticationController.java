package com.cmpe275.wiors.controller;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.Employer;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.EmployeeRegistrationRequest;
import com.cmpe275.wiors.model.EmployerRegistrationRequest;
import com.cmpe275.wiors.model.UserLoginRequest;
import com.cmpe275.wiors.service.EmailService;
import com.cmpe275.wiors.service.EmployeeService;
import com.cmpe275.wiors.service.EmployerService;
import com.cmpe275.wiors.util.CrossEntityValidations;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployerService employerService;
    @Autowired
    private CrossEntityValidations crossEntityValidations;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register/employee")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody EmployeeRegistrationRequest employee) throws RecordDoesNotExistException, BadRequestException {
        if (crossEntityValidations.emailAlreadyRegistered(employee.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        // Encrypt the password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeService.createEmployee(employee);
        return ResponseEntity.ok("Employee registered successfully");
    }

    @PostMapping("/register/employer")
    public ResponseEntity<?> registerEmployer(@Valid @RequestBody EmployerRegistrationRequest employer) throws BadRequestException {
        if (crossEntityValidations.emailAlreadyRegistered(employer.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        // Encrypt the password before saving
        employer.setPassword(passwordEncoder.encode(employer.getPassword()));
        employerService.createEmployer(employer);
        return ResponseEntity.ok("Employer registered successfully");
    }
    @PostMapping("/login/employee")
    public Employee loginEmployee(@Valid @RequestBody UserLoginRequest loginRequest) throws RecordDoesNotExistException, BadRequestException {
        return employeeService.handleLoginRequest(loginRequest);
    }
    @PostMapping("/login/employer")
    public Employer loginEmployer(@Valid @RequestBody UserLoginRequest loginRequest) throws RecordDoesNotExistException, BadRequestException {
        return employerService.handleLoginRequest(loginRequest);
    }
    @GetMapping("/validate/email")
    public ResponseEntity<?> validateEmail(@RequestParam String token) throws BadRequestException {
        emailService.validateEmail(token);
        return ResponseEntity.ok("Email validated successfully");
    }
}
