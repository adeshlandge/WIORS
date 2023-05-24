package com.cmpe275.wiors.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EmployeeId implements Serializable {
    @Column(name = "employee_id", nullable = false)
    private Long id;
    private String employerId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmployeeId other = (EmployeeId) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (getId() !=other.getId())
            return false;
        if (getEmployerId() == null) {
            if (other.getEmployerId() != null)
                return false;
        } else if (!getEmployerId().equals(other.getEmployerId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employerId);
    }
}
