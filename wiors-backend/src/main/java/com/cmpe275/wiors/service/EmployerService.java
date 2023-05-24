package com.cmpe275.wiors.service;


import com.cmpe275.wiors.entity.Address;
import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.EmployeeId;
import com.cmpe275.wiors.entity.Employer;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.CreateReservationRequest;
import com.cmpe275.wiors.model.EmployeeRegistrationRequest;
import com.cmpe275.wiors.model.EmployerRegistrationRequest;
import com.cmpe275.wiors.model.UserLoginRequest;
import com.cmpe275.wiors.repository.EmployeeRepository;
import com.cmpe275.wiors.repository.EmployerRepository;
import com.cmpe275.wiors.util.CrossEntityValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class EmployerService {
    /***
     * The Employer Repository
     */
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CrossEntityValidations crossEntityValidations;
    @Autowired
    private ReservationService reservationService;

    /**
     * Method to create an Employer
     * Calls the repository.save() method
     *
     * @param employerModel
     */
    public void createEmployer(EmployerRegistrationRequest employerModel) throws BadRequestException{
        Employer employer = new Employer();
        employer.setName(employerModel.getName());
        employer.setDescription(employerModel.getDescription());
        employer.setEmail(employerModel.getEmail());
        employer.setPassword(employerModel.getPassword());
        employer.setCapacity(employerModel.getCapacity());
        Address address = new Address();
        address.setCity(employerModel.getCity());
        address.setState(employerModel.getState());
        address.setStreet(employerModel.getStreet());
        address.setZip(employerModel.getZip());
        employer.setAddress(address);

        // save token for email verification
        UUID token = UUID.randomUUID();
        employer.setEmailValidationToken(token);
        employer.setIsEmailValidated(false);
        try {
            emailService.sendEmail(employer.getEmail(), employer.getName(), token.toString());
        }catch (Exception e){
            //Incase error occurs in email sending, just set that the email is validated
            employer.setIsEmailValidated(true);
            e.printStackTrace();
        }
        try{
            employerRepository.save(employer);

        }catch (DataIntegrityViolationException e){
            throw new BadRequestException(e.getMessage());
        }

    }
    public Employer handleLoginRequest(UserLoginRequest employerLogin) throws RecordDoesNotExistException, BadRequestException {
        String email = employerLogin.getEmail();
        String password = employerLogin.getPassword();
        Employer employer = employerRepository.findByEmail(email);
        if(employer==null){
            throw new RecordDoesNotExistException("Employer with email id "+email+" not found");
        }
        if( employer.getIsEmailValidated() == null){
            throw new BadRequestException("You can't login without verifying your email. Check your inbox");}

        if(employerLogin.isGoogle() == true) {
            return employer;
        }
        if(!passwordEncoder.matches(password,employer.getPassword())){
            //Successful authentication
            throw new BadRequestException("Incorrect password");
        }
        return employer;
    }
    @Transactional
    public Employer updateMop(String employerId, Integer mop) throws BadRequestException, RecordDoesNotExistException {
        if(mop<0 && mop>5) {
            throw new BadRequestException("Mop should be between 0 and 5");
        }
        Optional<Employer> optionalEmployer = employerRepository.findById(employerId);
        if (optionalEmployer.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employer with id:%s does not exist."));
        }
        Employer employer = optionalEmployer.get();
        List<Employee> employees = employeeRepository.findByEmployerId(employerId);
        List<Employee> employeesToBeUpdated= new ArrayList<>();
        for (Employee employee: employees) {
            if (employee.getMop()!=null && employee.getMop()>mop) {
                continue;
            }
            employee.setMop(mop);
            employeesToBeUpdated.add(employee);
        }
        employer.setMop(mop);
        employeeRepository.saveAll(employees);
        employerRepository.save(employer);
        return employer;
    }

    public Employer cancelMop(String employerId) throws RecordDoesNotExistException, BadRequestException {


        Optional<Employer> employer = employerRepository.findById(employerId);


        if(employer.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employer with employerId  not found",
                    employerId));
        }

        employer.get().setMop(0);

        Employer savedemployer = employerRepository.save(employer.get());

        List<Employee> employees = employeeRepository.findByEmployerId(employerId);

        employees.forEach((employee -> employee.setMop(0)));

        employeeRepository.saveAll(employees);

        return savedemployer;
    }

    @Transactional
    public void handleBulkEmployeeRegistration(String employerId, MultipartFile file) throws BadRequestException,IOException {
        List<String> validationErrors = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length <3 || data.length > 4) {
                    validationErrors.add("Invalid CSV file format");
                    break;
                }
                else if (crossEntityValidations.emailAlreadyRegistered(data[0].trim())) {
                    validationErrors.add("Email- "+data[0].trim()+" is already registered");
                }
                else {
                    EmployeeRegistrationRequest employeeRegistrationRequest = new EmployeeRegistrationRequest();
                    employeeRegistrationRequest.setEmail(data[0].trim());
                    employeeRegistrationRequest.setName(data[1].trim());
                    employeeRegistrationRequest.setPassword(passwordEncoder.encode(data[2].trim()));
                    employeeRegistrationRequest.setEmployerId(employerId);
                    if(data.length == 4 && !data[3].isBlank()){
                        employeeRegistrationRequest.setManagerEmailId(data[3].trim());
                    }
                    employeeService.createEmployee(employeeRegistrationRequest);
                }
            }
            if(!validationErrors.isEmpty()){
                throw new BadRequestException(String.join(", ", validationErrors));
            }
        } catch (IOException | RecordDoesNotExistException e) {
            e.printStackTrace();
            throw new BadRequestException("Could not register employees");}
    }

    @Transactional
    public void handleBulkSeatReservation(String employerId, MultipartFile file) throws BadRequestException, IOException {
        List<String> validationErrors = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            Optional<Employer> employer = employerRepository.findById(employerId);
            //If Employer not found, throw the Employer not found exception right away.
            if(employer.isEmpty()) {
                validationErrors.add(String.format("Employer with id:%s not found", employerId));
                throw new BadRequestException(String.join(", ", validationErrors));
            }
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String startDate = data[1].trim();
                String endDate = data[2].trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate parsedStartDate = LocalDate.parse(startDate, formatter);
                LocalDate parsedEndDate = LocalDate.parse(endDate, formatter);
                if (data.length <3 || data.length > 4) {
                    validationErrors.add("Invalid CSV file format");
                    break;
                } else if (parsedStartDate.isBefore(LocalDate.now()) || parsedEndDate.isBefore(LocalDate.now())) {
                    validationErrors.add("Invalid date range: Start date and end date must be greater than or equal to today.");
                    break;
                } else{
                    CreateReservationRequest createReservationRequest = new CreateReservationRequest();
                    createReservationRequest.setEmailId(data[0].trim());
                    createReservationRequest.setStartDate(parsedStartDate);
                    createReservationRequest.setEndDate(parsedEndDate);
                    reservationService.createSeatReservation(createReservationRequest, true, null);
                }
            }
            if(!validationErrors.isEmpty()){
                throw new BadRequestException(String.join(", ", validationErrors));
            }
        } catch (IOException | RecordDoesNotExistException e) {
            e.printStackTrace();
            throw new BadRequestException("Could not reserve seats for employees");}
    }

    public Employer updateCapacity(String employerId, Integer capacity) throws BadRequestException, RecordDoesNotExistException {
        System.out.println(capacity);

        if(capacity < 3 || capacity > 100) {
            throw new BadRequestException("Capacity should be between 3 and 100");
        }

        Optional<Employer> optionalEmployer = employerRepository.findById(employerId);

        if (optionalEmployer.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employer with id:%s does not exist."));
        }
        System.out.println("op2");
        Employer employer = optionalEmployer.get();

        Integer currentCapacity = employer.getCapacity();

        if (capacity < currentCapacity ) {
            throw new BadRequestException("Decreasing capacity not allowed");
        }

        employer.setCapacity(capacity);
        try{
            employerRepository.save(employer);
        }

        catch (Error e) {
            throw new BadRequestException(e.getMessage());
        }

        return employer;
    }

    public List<Employer> getAllEmployers(){
        List<Employer> employers = employerRepository.findAll();
        return employers;
    }
    public Employer findEmployer(String id) throws RecordDoesNotExistException {
        return employerRepository.findById(id).orElseThrow(() ->
                new RecordDoesNotExistException(
                        String.format("Employer with id: %s does not exist", id)
                )
        );
    }
}
