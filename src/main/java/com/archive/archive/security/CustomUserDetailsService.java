package com.archive.archive.security;

import com.archive.archive.employee.Employee;
import com.archive.archive.employee.EmployeeRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepo employeeRepo;

    public CustomUserDetailsService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        Employee employee = employeeRepo.findByLogin(login);

        if (employee == null){
            throw new UsernameNotFoundException("Сотрудник не найден: " + login);
        }


        return new CustomUserPrincipal(
                employee.getId(),
                employee.getLogin(),
                employee.getPassword(),
                employee.getRole()
        );
    }
}