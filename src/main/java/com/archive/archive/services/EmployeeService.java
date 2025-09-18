package com.archive.archive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.archive.archive.models.Employee;
import com.archive.archive.repositories.EmployeeRepo;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class EmployeeService implements UserDetailsService {
    @Autowired
    EmployeeRepo employeeRepo;

    public List<Employee> getAll(){
        return employeeRepo.findAll();
    }

    public List<Employee> getAllSortedAsc(){
        return employeeRepo.findAll(Sort.by("lastName"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepo.findByLogin(username);

        if (employee == null) {
            throw new UsernameNotFoundException("Employee not found");
        }

        return employee;
    }

}



