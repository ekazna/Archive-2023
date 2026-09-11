package com.archive.archive.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.models.Employee;
import com.archive.archive.repositories.EmployeeRepo;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepo employeeRepo;

    public EmployeeService(EmployeeRepo employeeRepo){
        this.employeeRepo = employeeRepo;
    }

    public List<Employee> getAll(){
        return employeeRepo.findAll();
    }

    public List<Employee> getAllSortedAsc(){
        return employeeRepo.findAll(Sort.by("lastName"));
    }


}



