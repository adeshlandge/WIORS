package com.cmpe275.wiors.repository;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.EmployeeId;
import com.cmpe275.wiors.entity.SeatReservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReservationRepository extends JpaRepository<SeatReservation, Long> {


    @Query(value = "SELECT\n" +
            "    COUNT(*) AS reservationCount\n" +
            "FROM seat_reservation sr\n" +
            "WHERE sr.date BETWEEN ?1 AND DATE_ADD(?1, INTERVAL 4 DAY)\n" +
            "    AND sr.employee_employer_id = ?2\n" +
            "    AND sr.employee_employee_id = ?3\n" +
            "GROUP BY sr.employee_employer_id, sr.employee_employee_id", nativeQuery = true)
    Integer getReservationCountForEmployee(LocalDate startOfTheWeek, String employerId, Long employeeId);

    @Query(value = "SELECT COUNT(*) FROM seat_reservation sr WHERE sr.employee_employee_id = :employeeId AND sr.employee_employer_id = :employerId AND sr.date <= :endDate AND sr.date >= :startDate", nativeQuery = true)
    public int getEmployeeReservationCountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("employerId") String employerId, @Param("employeeId") Long employeeId);

    @Query(value = "SELECT * FROM seat_reservation sr WHERE sr.employee_employee_id = :employeeId AND sr.employee_employer_id = :employerId AND sr.date <= :endDate AND sr.date >= :startDate", nativeQuery = true)
    public List<SeatReservation> getEmployeeReservationByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("employerId") String employerId, @Param("employeeId") Long employeeId);

    @Query(value = "SELECT * FROM seat_reservation sr WHERE  sr.employee_employer_id = :employerId AND sr.date = :startDate", nativeQuery = true)
    public List<SeatReservation> getReservations(@Param("startDate") LocalDate startDate, @Param("employerId") String employerId);

    @Query(value = "SELECT * FROM seat_reservation sr WHERE  sr.employee_employer_id = ?1 AND sr.employee_employee_id = ?2", nativeQuery = true)
    public List<SeatReservation> getAllEmployeeReservations( String employerId, Long employeeId);


//  // Custom function: Find SeatReservations by date
//  List<SeatReservation> findByDate(LocalDate date);
//
//  // Custom function: Find SeatReservations by employee and date
//  List<SeatReservation> findByEmployeeIdAndDate(EmployeeId employeeId, LocalDate date);
//
//  // Custom function: Find SeatReservations where isSelfReservation is true
//  List<SeatReservation> findByIsSelfReservationTrue();

    @Query("SELECT sr FROM SeatReservation sr WHERE sr.employee = :employee")
    List<SeatReservation> findByEmployee(@Param("employee") Employee employee);

    @Query(value=
            "SELECT e.employee_id\n" +
                    "FROM seat_reservation sr\n" +
                    "JOIN employee e ON sr.employee_employee_id = e.employee_id \n" +
                    "WHERE sr.date >= :startDate AND  sr.date <=:endDate  AND sr.employee_employer_id = :employerId  \n" +
                    "GROUP BY e.employee_id, e.mop\n" +
                    "HAVING COUNT(*) > e.mop;"
            , nativeQuery = true)
    public List<Long> getPremptableEmployeeIds(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("employerId") String employerId);

    @Query(value ="SELECT *  FROM seat_reservation  sr WHERE  sr.employee_employer_id = :employerId AND sr.employee_employee_id = :employeeId AND sr.date = :date LIMIT 1;", nativeQuery = true)
    public SeatReservation getReseravationByDateAndEmployeeID(@Param("date") LocalDate date, @Param("employerId") String employerId, @Param("employeeId") Long employeeId);
//    @Query(value = "SELECT * FROM seat_reservation  sr\n" +
//            "WHERE sr.date >= :startDate AND  sr.date <=:endDate")
    List<SeatReservation> findByDateBetween(LocalDate startDate, LocalDate endDate);



    @Transactional
    @Modifying
    @Query("DELETE FROM SeatReservation sr WHERE sr.gtdReservedBy = :gtdReservedBy")
    void deleteByGtdReservedBy(Optional<Employee> gtdReservedBy);

    List<SeatReservation> findByGtdReservedBy(Optional<Employee> gtdReservedBy);



    @Transactional
    @Modifying
    @Query("DELETE FROM SeatReservation sr WHERE sr.employee.employer.id = :employerId")
    void deleteAllByEmployerId(String employerId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SeatReservation sr WHERE sr.employee = :employee")
    void deleteAllByEmployeeId(Optional<Employee> employee) ;

}
