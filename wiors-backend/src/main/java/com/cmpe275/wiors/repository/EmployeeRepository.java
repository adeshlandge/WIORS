package com.cmpe275.wiors.repository;



import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, EmployeeId> {
    @Query(value = "SELECT COALESCE(MAX(employee_id),0) FROM employee WHERE employee.employer_id = ?1", nativeQuery = true)
    Long getNextSequence( String employerId);

    Employee findByEmail(String email);
    Boolean existsByEmail(String email);
    List<Employee> findByEmployerId(String employerId);
    //@Query("SELECT * FROM Employee e WHERE e.manager_employee_id = :managerId")
    List<Employee> findByManagerEmployeeId(EmployeeId managerId);
    Employee findByEmailValidationToken(UUID token);

}
