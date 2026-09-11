package com.archive.archive.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Integer>{
    Employee findByLogin(String login);
}
