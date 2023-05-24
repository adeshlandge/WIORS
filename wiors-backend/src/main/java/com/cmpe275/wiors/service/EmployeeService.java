package com.cmpe275.wiors.service;



import com.cmpe275.wiors.entity.*;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.CreateReservationRequest;
import com.cmpe275.wiors.model.DashboardAnalyticsRequest;
import com.cmpe275.wiors.model.EmployeeRegistrationRequest;
import com.cmpe275.wiors.model.UserLoginRequest;
import com.cmpe275.wiors.repository.EmployeeRepository;
import com.cmpe275.wiors.repository.EmployerRepository;
import com.cmpe275.wiors.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    public void createEmployee(EmployeeRegistrationRequest employeeModel)throws RecordDoesNotExistException, BadRequestException {
        //Create an Employee Entity
        Employee employee = new Employee();
        String employerId = employeeModel.getEmployerId();
        //Find the Employer entity corresponding to the passed employerId
        Optional<Employer> employer = employerRepository.findById(employerId);
        //If Employer not found, throw the Record not Found exception
        if(employer.isEmpty()) {
            throw new RecordDoesNotExistException(String.format("Employer with id:%s not found",
                    employerId));
        }
        //Retrieve details of the Manager for an employee, if passed
        String managerEmailId = employeeModel.getManagerEmailId();
        if (managerEmailId!=null && !managerEmailId.isEmpty()) {
            //Call an inner function to find the manager details
            Employee manager = employeeRepository.findByEmail(managerEmailId);
            if(manager==null){
                throw new RecordDoesNotExistException(String.format("Manager with email id:%s not found",
                        managerEmailId));
            }
            employee.setManager(manager);
        }
        //Set the Employer in Employee entity
        employee.setEmployer(employer.get());
        //Since auto increment is not working, find the next employee ID in sequence
        //Revisit this custom query
        Long nextVal = employeeRepository.getNextSequence(employerId)+1;
        EmployeeId eId = new EmployeeId(nextVal, employerId);
        employee.setEmployeeId(eId);
        //Set the rest of the fields from the registration request
        employee.setName(employeeModel.getName());
        employee.setEmail(employeeModel.getEmail());
        employee.setPassword(employeeModel.getPassword());
        //Set the optional fields
        employee.setTitle(employeeModel.getTitle());
        // Create address object
        Address address = new Address();
        address.setCity(employeeModel.getCity());
        address.setState(employeeModel.getState());
        address.setStreet(employeeModel.getStreet());
        address.setZip(employeeModel.getZip());
        employee.setAddress(address);
        //Determine the mop and gtd for the employee
        //---to do
        // create  a token and save it in repo

        UUID token = UUID.randomUUID();
        employee.setEmailValidationToken(token);
        employee.setIsEmailValidated(false);
        try {
            emailService.sendEmail(employee.getEmail(), employee.getName(), token.toString());
        }catch (Exception e){
            //Incase error occurs in email sending, just set that the email is validated
            employee.setIsEmailValidated(true);
            e.printStackTrace();
        }

        try{
            employeeRepository.save(employee);
        }catch (DataIntegrityViolationException e){
            throw new BadRequestException(e.getMessage());
        }

    }
    public Employee handleLoginRequest(UserLoginRequest employeeLogin) throws RecordDoesNotExistException, BadRequestException {
        String email = employeeLogin.getEmail();
        String password = employeeLogin.getPassword();
        boolean google = employeeLogin.isGoogle();
        System.out.println( "isGoogle " +  email + "," + password + "," + employeeLogin.isGoogle());

        Employee employee = employeeRepository.findByEmail(email);
        if(employee==null){
            throw new RecordDoesNotExistException("User with email id "+email+" not found");
        }
        if(employee.getIsEmailValidated() == null){
            throw new BadRequestException("You can't login without verifying your email. Check your inbox");
        }
        System.out.println( "isGoogle " + employeeLogin.isGoogle());
        if(employeeLogin.isGoogle () == true) {
            return employee;
        }

        if(!passwordEncoder.matches(password,employee.getPassword())){
            //Successful authentication
            throw new BadRequestException("Incorrect password");
        }
        return employee;
    }

    public Employee updateMop(String employerId, Long employeeId, Integer mop) throws RecordDoesNotExistException, BadRequestException {
        System.out.println(employeeId + ","+ "employerId");
        EmployeeId employeeIdObject = new EmployeeId(employeeId, employerId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeIdObject);
        if(mop<0 || mop>5) {
            throw new BadRequestException("Mop should be between 0 and 5");
        }
        if(optionalEmployee.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employee with employerId %s and employeeId %s not found",
                    employerId, employeeId));
        }
        Employee employee = optionalEmployee.get();
        if(employee.getReports().isEmpty()) {
            throw new BadRequestException("Employee with no reports cannot update MOP");
        }
        if(employee.getManager()!=null) {
            Employee manager = employee.getManager();
            if(manager.getMop()!=null && mop < manager.getMop()) {
                throw new BadRequestException("Employee cannot set MOP which is less than his manager's MOP ");
            }
        }
        // All validations pass
        Queue<Employee> queue = new LinkedList<>();
        List<Employee> employeesToBeUpdated = new ArrayList<>();
        queue.add(employee);
        while (!queue.isEmpty()) {
            Employee temp = queue.poll();
            temp.setMop(mop);
            employeesToBeUpdated.add(temp);
            for(Employee localE: temp.getReports()) {
                if(localE.getMop()!=null && localE.getMop()>=mop) {
                    // No need to process if the employee already has mop>mop to be set
                    continue;
                }
                queue.add(localE);
            }
        }
        employeeRepository.saveAll(employeesToBeUpdated);
        return employee;
    }


    public Employee cancelMop(String employerId, Long employeeId) throws RecordDoesNotExistException, BadRequestException {
        System.out.println(employeeId + ","+ "employerId");

        EmployeeId employeeIdObject = new EmployeeId(employeeId, employerId);

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeIdObject);

        if(optionalEmployee.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employee with employerId %s and employeeId %s not found",
                    employerId, employeeId));
        }

        Employee employee = optionalEmployee.get();

        if(employee.getReports().isEmpty()) {
            throw new BadRequestException("Employee with no reports cannot update MOP");
        }
        int newMop = 0;
        if(employee.getManager()!=null) {
            Employee manager = employee.getManager();
            if(manager.getMop()!=null) {
                newMop = manager.getMop();
            }
            else{
                newMop = employee.getEmployer().getMop();
            }
        }
        // All validations pass
        Queue<Employee> queue = new LinkedList<>();
        List<Employee> employeesToBeUpdated = new ArrayList<>();
        queue.add(employee);
        while (!queue.isEmpty()) {
            Employee temp = queue.poll();
            temp.setMop(newMop);
            employeesToBeUpdated.add(temp);
            for(Employee localE: temp.getReports()) {
//                if(localE.getMop()!=null && localE.getMop()>=mop) {
//                    // No need to process if the employee already has mop>mop to be set
//                    continue;
//                }
                queue.add(localE);
            }
        }
        employeeRepository.saveAll(employeesToBeUpdated);
        return employee;
    }

    @Transactional
    public Employee specifyGtd(String employerId, Long employeeId, DayOfWeek gtd) throws BadRequestException, RecordDoesNotExistException {
        EmployeeId employeeIdObject = new EmployeeId(employeeId, employerId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeIdObject);
        //Check if employee exists
        if(optionalEmployee.isEmpty()){
            throw new RecordDoesNotExistException(String.format("Employee with employerId %s and employeeId %s not found",
                    employerId, employeeId));
        }
        //Check if employee is a manager
        Employee manager = optionalEmployee.get();
        if(manager.getReports() == null || manager.getReports().size() == 0){
            throw new BadRequestException("Only managers can specify Get Together Days");
        }
        //The get together day must be a weekday
        if(gtd== DayOfWeek.SUNDAY || gtd == DayOfWeek.SATURDAY){
            throw new BadRequestException("You can specify only weekdays as Get Together Days");
        }
        //Find the gtd days for the next 10 weeks and call create seat reservation for all the direct reports
        LocalDate currentDate = LocalDate.now();
        List<Employee> employeesToBeUpdated = new ArrayList<>();
        employeesToBeUpdated.add(manager);
        List<Employee> reportees = manager.getReports();
        employeesToBeUpdated.addAll(reportees);
        CreateReservationRequest request;
        for (int i = 0; i < 10; i++) {
            LocalDate nextDate = currentDate.with(TemporalAdjusters.next(gtd));
            //Call seat reservation for the current employee as well as all their reports
            for(Employee employee: employeesToBeUpdated){
                request = new CreateReservationRequest(employee.getEmail(), nextDate, nextDate);
                reservationService.createSeatReservation(request, false, manager);
            }
            currentDate = nextDate;
        }
        //Also update the Gtd
        for(Employee employee: employeesToBeUpdated){
            employee.setGtd(gtd);
        }
        employeeRepository.saveAll(employeesToBeUpdated);
        return manager;
    }

    public Optional<Employee> cancelGtd(String employerId, Long employeeId) throws BadRequestException {
        return reservationService.deleteGTDReservations(new EmployeeId(employeeId, employerId));
    }
    public Employee findEmployee(String employerId, Long id, String format) throws RecordDoesNotExistException {
        EmployeeId employeeId = new EmployeeId(id, employerId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) {
            throw new RecordDoesNotExistException("Record does nt exits");
        }
        return optionalEmployee.get();
    }


    public DashboardAnalyticsRequest fetchAnalytics(String startDateValue, String endDateValue, String employerId, Long employeeId) throws RecordDoesNotExistException, BadRequestException {
        EmployeeId employeeIdObject = new EmployeeId(employeeId, employerId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeIdObject);
        if(optionalEmployee==null){
            throw new RecordDoesNotExistException(String.format("Employee with Id: %s not found", employeeId));
        }
        LocalDate startDate = LocalDate.parse(startDateValue);
        LocalDate endDate = LocalDate.parse(endDateValue);
        if(startDate.isAfter(endDate)) {
            throw new BadRequestException("Start Date is greater than endDate");
        }
        List<Employee> employees = employeeRepository.findByManagerEmployeeId(employeeIdObject);
        List<SeatReservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);

        int totalMops = employees.stream()
                .mapToInt(Employee::getMop)
                .sum();
        System.out.println("totalMops==>"+totalMops);

        long fulfilledGTDs = employees.stream()
                .filter(e->e.getGtd() == optionalEmployee.get().getGtd()).count();
        System.out.println("fulfilledGTDs==>"+fulfilledGTDs);
        long totalReservations = reservations.stream().
                filter(reservation ->
                        employees.stream().
                                anyMatch(e -> e.getEmployeeId().getId().equals(reservation.getEmployee().getEmployeeId().getId()))).count();
        System.out.println("totalReservations==>"+totalReservations);
        long overallAttendanceMeetRate = totalMops == 0 ? 100 :(totalReservations + fulfilledGTDs)*100 / totalMops;
        System.out.println("overallAttendanceMeetRate==>"+overallAttendanceMeetRate);

//        long totalEmployeesMeetingMop = employees.stream()
//                .filter(employee -> employee.getSeatReservationsList().size() >= employee.getMop())
//                .count();
        long employeesMeetingMOP = employees.stream()
                .filter(employee -> employee.getSeatReservationsList().stream()
                        .filter(reservation -> reservation.getDate().isAfter(startDate) && reservation.getDate().isBefore(endDate))
                        .count() >= employee.getMop())
                .count();
        System.out.println("totalEmployeesMeetingMop==>"+employeesMeetingMOP);
        long employeeComplianceRate = employees.size() == 0 ? 100: (employeesMeetingMOP/employees.size())*100;
        System.out.println("employeeComplianceRate==>"+employeeComplianceRate);

        int employerCapacityPerDay = employerRepository.findById(employerId).get().getCapacity();
        System.out.println("Employer Capacity Per Day: " + employerCapacityPerDay);

        long employeesNotMeetingMOP = employees.stream()
                .mapToLong(employee -> Math.max(0, employee.getMop() - employee.getSeatReservationsList().stream()
                        .filter(reservation -> reservation.getDate().isAfter(startDate) && reservation.getDate().isBefore(endDate))
                        .count()))
                .sum();

        long additionalSeatsRequired = Math.max(0, employeesNotMeetingMOP - (employerCapacityPerDay * 5));

        DashboardAnalyticsRequest dashboardResponse = new DashboardAnalyticsRequest();
        dashboardResponse.setStartDate(startDate);
        dashboardResponse.setEndDate(endDate);
        dashboardResponse.setOverAllAttendanceMeetRate(overallAttendanceMeetRate);
        dashboardResponse.setEmployeeComplianceRate(employeeComplianceRate);
        dashboardResponse.setAdditionalSeatsRequired(additionalSeatsRequired);
        return dashboardResponse;
    }
}
