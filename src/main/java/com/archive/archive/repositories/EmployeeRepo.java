package com.archive.archive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer>{
    Employee findByLogin(String login);
}
