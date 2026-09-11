package com.archive.archive.employee;

import com.archive.archive.employee.dto.EmployeeOptionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeOptionResponse> getEmployees() {
        return employeeService.getAll().stream()
                .map(employee -> new EmployeeOptionResponse(
                        employee.getId(),
                        employee.getLogin(),
                        employee.getEmail()
                ))
                .toList();
    }
}