package com.archive.archive.security;

import com.archive.archive.models.Employee;
import com.archive.archive.repositories.EmployeeRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final EmployeeRepo employeeRepo;

    public CurrentUserService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public CustomUserPrincipal getPrincipal() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No authenticated user"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserPrincipal customUserPrincipal)) {
            throw new IllegalStateException(
                    "Authenticated principal has unexpected type"
            );
        }

        return customUserPrincipal;
    }

    public Employee getCurrentEmployee() {

        Integer employeeId =
                getPrincipal().getEmployeeId();

        return employeeRepo.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated employee not found: "
                                        + employeeId
                        )
                );
    }
}