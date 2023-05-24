package com.cmpe275.wiors.service;
import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.EmployeeId;
import com.cmpe275.wiors.entity.Employer;
import com.cmpe275.wiors.entity.SeatReservation;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.exception.RecordDoesNotExistException;
import com.cmpe275.wiors.model.CreateReservationRequest;
import com.cmpe275.wiors.model.SeatAvailabilityByDate;
import com.cmpe275.wiors.model.SeatAvailabilityRequest;
import com.cmpe275.wiors.model.SeatAvailabilityResponse;
import com.cmpe275.wiors.repository.EmployeeRepository;
import com.cmpe275.wiors.repository.EmployerRepository;
import com.cmpe275.wiors.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Transactional
    public List<SeatReservation> createSeatReservation(CreateReservationRequest request, Boolean isSelfReserved, Employee  gtdReservedBy) throws RecordDoesNotExistException, BadRequestException {
        Employee employee = employeeRepository.findByEmail(request.getEmailId());

        if(employee != null){
            List<SeatReservation> seatReservations = new ArrayList<>();

            //Employee found
            //Do Validation checks as to if the seats can be reserved for the specified dates
            //Expectation is that UI will send only dates within a work week

            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();


            if(startDate.isAfter(endDate)) {

                throw new BadRequestException("Start Date is greater than endDate");

            }

            if (!areDatesInSameWeekWithoutWeekend(startDate, endDate)) {
                // Dates span across multiple work weeks or outside the work week
                throw new BadRequestException("Reservations can be made only for dates falling within a " +
                        "single workweek" + "and weekend should be included");
            }

            List<LocalDate> dateList =  createDateList(startDate, endDate);

            int alreadyReservedCount = reservationRepository.getEmployeeReservationCountByDateRange(startDate, endDate, employee.getEmployer().getId(), employee.getEmployeeId().getId());

            if ( alreadyReservedCount > 0) {
                throw  new BadRequestException("Duplicate Reservation exist for the employee with email - "+request.getEmailId()+" within the date range");
            }


            int capacity = employee.getEmployer().getCapacity();

            LocalDate startOfWeek = getStartOfWeek(startDate);
            LocalDate endOfWeek = getEndOfWeek(startDate);

            List<SeatReservation> seatReservationsWithWeek = reservationRepository.getEmployeeReservationByDateRange(startOfWeek, endOfWeek, employee.getEmployer().getId(), employee.getEmployeeId().getId());

            int reqCompliance =  employee.getMop() - seatReservationsWithWeek.size() ;

            if (dateList.size() > reqCompliance) {
                throw new BadRequestException("No additional seats available, no of dates go beyond mop");
            }

            // should throw error if not available
            for (LocalDate date: dateList) {

               // check if there is any seat available
                List<SeatReservation> totalSeatReservationsForDay = reservationRepository.getReservations(date, employee.getEmployer().getId());



                if(totalSeatReservationsForDay.size() >= capacity){
                    // check if employee meets MOP for the week
                    if (reqCompliance > 0) {

                        // check for premptable and override  it
                        // decrease reqCompliance

                       List<Long> premptableEmployeeIds = reservationRepository.getPremptableEmployeeIds(startOfWeek, endOfWeek, employee.getEmployer().getId() );

                        if (premptableEmployeeIds.size() > 0){
                            SeatReservation seatReservation = reservationRepository.getReseravationByDateAndEmployeeID(date,employee.getEmployer().getId(),premptableEmployeeIds.get(0));

                            SeatReservation modifiedReservation = new SeatReservation();
                            modifiedReservation.setId(seatReservation.getId());
                            modifiedReservation.setEmployee(employee);
                            modifiedReservation.setDate(date);
                            modifiedReservation.setIsSelfReservation(isSelfReserved);
                            if (gtdReservedBy != null) {
                                modifiedReservation.setGtdReservedBy(gtdReservedBy);
                            }

                            seatReservations.add(modifiedReservation);
                        }
                        else {
                            throw new BadRequestException("Does not meet MOP, beyond capacity, and no premptable seat :  " + date);
                        }


                    }
                    else {
                        throw new BadRequestException("Meets MOP, trying to book beyond capacit:   " + date);
                    }
                } else {
                    SeatReservation seatReservation = new SeatReservation();
                    seatReservation.setEmployee(employee);
                    seatReservation.setDate(date);
                    seatReservation.setIsSelfReservation(isSelfReserved);
                    seatReservations.add(seatReservation);
                    if(gtdReservedBy != null){
                        seatReservation.setGtdReservedBy(gtdReservedBy);
                    }

                }
            }

            try{
                return reservationRepository.saveAll(seatReservations);
            }catch (DataIntegrityViolationException e){
                throw new BadRequestException(e.getMessage());
            }

        }else {
            throw new RecordDoesNotExistException("Employee with email "+request.getEmailId()+" not found");
        }
    }


    public Optional<Employee> deleteGTDReservations(EmployeeId employeeId) throws BadRequestException {
        try {
            Optional<Employee> gtdReservedBy = employeeRepository.findById(employeeId);
            gtdReservedBy.get().setGtd(null);
            employeeRepository.save(gtdReservedBy.get());
            List<SeatReservation> reservations = reservationRepository.findByGtdReservedBy(gtdReservedBy);
            reservationRepository.deleteByGtdReservedBy(gtdReservedBy);;
            return gtdReservedBy;
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }



    public boolean areDatesInSameWeekWithoutWeekend(LocalDate date1, LocalDate date2) {
        // Check if both dates fall within the same week
        int week1 = date1.get(WeekFields.ISO.weekOfYear());
        int week2 = date2.get(WeekFields.ISO.weekOfYear());
        if (week1 != week2) {
            return false;
        }

        // Check if none of the dates are weekends (Saturday or Sunday)
        DayOfWeek dayOfWeek1 = date1.getDayOfWeek();
        DayOfWeek dayOfWeek2 = date2.getDayOfWeek();
        if (dayOfWeek1 == DayOfWeek.SATURDAY || dayOfWeek1 == DayOfWeek.SUNDAY ||
                dayOfWeek2 == DayOfWeek.SATURDAY || dayOfWeek2 == DayOfWeek.SUNDAY) {
            return false;
        }

        return true;
    }

    public static LocalDate getStartOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    public static LocalDate getEndOfWeek(LocalDate date) {
        return date.plusDays(DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue());
    }

    public List<LocalDate> createDateList(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dateList;
    }




    /**
     * This method needs to take in the Start of the Week Date passed,
     * Employer ID and Employeed ID and
     * return the seat availability response of the week - next five days
     * based on employer capacity, employee mop and already booked reservations
     * @param request
     * @return
     * noOfDaysLeftToMeetMOP for the employee for this week
     * An arraylist[5] of SeatAvailabilityByDate for the next five days
     * Seat AvailabilityByDate has 1. Date, 2. IsAvailable by Capacity, 3. CanBeMadeAvailableByPre-emption
     */
    public SeatAvailabilityResponse getSeatAvailability(SeatAvailabilityRequest request){
        LocalDate startOfTheWeek = request.getStartOfTheWeek().with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        EmployeeId employeeIdObject = new EmployeeId(request.getEmployeeId(), request.getEmployerId());
        List<SeatAvailabilityByDate> seatAvailabilityList = getSeatAvailability(startOfTheWeek,
                request.getEmployerId(), request.getEmployeeId());


        SeatAvailabilityResponse response = SeatAvailabilityResponse.builder()
                .seatAvailabilityByDateList(seatAvailabilityList)
                .noOfDaysLeftToMeetMOP(getNumberOfDaysToMeetMOP(startOfTheWeek, employeeIdObject))
                .build();

        return response;
    }

    private List<SeatAvailabilityByDate> getSeatAvailability(LocalDate startOfTheWeek, String employerId, Long employeeId){
        //Removed the reservation repository availability method call as this is no longer correct because of deletion of
        //isOverMOP field
        List<Object> resultList = null;
        List<SeatAvailabilityByDate> seatAvailabilityList = new ArrayList<>();
        for (Object result : resultList) {
            Object[] row = (Object[]) result;
            LocalDate date = LocalDate.parse((String) row[0]);
            Boolean isAvailable = ((Number) row[1]).intValue() != 0;
            Boolean canBePreempted = ((Number) row[2]).intValue() != 0;
            SeatAvailabilityByDate seatAvailability = new SeatAvailabilityByDate(date, isAvailable, canBePreempted);
            seatAvailabilityList.add(seatAvailability);
        }
        return seatAvailabilityList;
    }
    private Integer getNumberOfDaysToMeetMOP(LocalDate startOfTheWeek, EmployeeId employeeId){
        Integer reservationCount = reservationRepository.getReservationCountForEmployee(startOfTheWeek, employeeId.getEmployerId(), employeeId.getId());
        int numberOfSeatsBookedByEmployee = reservationCount != null ? reservationCount.intValue() : 0;
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        Employee employee = optionalEmployee.get();
        return employee.getMop()-numberOfSeatsBookedByEmployee;
    }

    public List<SeatReservation> getEmployeeAllReservations(String employerId,Long  employeeId){
        Optional<Employee> employee = employeeRepository.findById(new EmployeeId(employeeId, employerId));
        List<SeatReservation> seatReservations = reservationRepository.findByEmployee(employee.get());
        return seatReservations;
    }
    public Optional<Employer> deleteAllEmployerReservations(String employerId){
        reservationRepository.deleteAllByEmployerId(employerId);
        return employerRepository.findById(employerId);

    }
    public Optional<Employee> deleteEmployeeReservations(String employerId, Long  employeeId){
        Optional<Employee> employee = employeeRepository.findById(new EmployeeId(employeeId,employerId));
        reservationRepository.deleteAllByEmployeeId(employee);
        return employee;
    }

    public Object deleteReservationById(Long id) {
        reservationRepository.deleteById(id);
        return null;
    }
}
