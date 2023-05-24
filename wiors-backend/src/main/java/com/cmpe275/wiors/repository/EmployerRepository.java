package com.cmpe275.wiors.repository;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {
    Employer findByEmail(String email);
    Boolean existsByEmail(String email);
    Employer findByEmailValidationToken(UUID token);
}
