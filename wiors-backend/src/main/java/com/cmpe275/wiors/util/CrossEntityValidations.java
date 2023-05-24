package com.cmpe275.wiors.util;

import com.cmpe275.wiors.entity.Employee;
import com.cmpe275.wiors.entity.Employer;
import com.cmpe275.wiors.exception.BadRequestException;
import com.cmpe275.wiors.repository.EmployeeRepository;
import com.cmpe275.wiors.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CrossEntityValidations {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployerRepository employerRepository;
    public Boolean emailAlreadyRegistered(String email){
        return (employeeRepository.existsByEmail(email) || employerRepository.existsByEmail(email));
    }
    public Boolean validateEmail(String token) throws BadRequestException {
        UUID tokenUuid = UUID.fromString(token);
        //Check if the token exists in the Employer table
        Employer employer = employerRepository.findByEmailValidationToken(tokenUuid);
        if( employer != null){
            employer.setIsEmailValidated(true);
            employerRepository.save(employer);
            return true;
        } else{
            Employee employee = employeeRepository.findByEmailValidationToken(tokenUuid);
            if(employee != null){
                employee.setIsEmailValidated(true);
                employeeRepository.save(employee);
                return true;
            }
        }
        throw new BadRequestException("Invalid token");
    }

}
