package com.archive.archive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.archive.archive.services.EmployeeService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final EmployeeService employeeService;

    public WebSecurityConfig(EmployeeService employeeService){
        this.employeeService = employeeService;
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER")
                .anyRequest().authenticated()
            )
            .userDetailsService(employeeService)
            .headers(headers -> headers.frameOptions().sameOrigin())
            .formLogin(login -> login
                .loginPage("/login")
			    .permitAll()
                .defaultSuccessUrl("/default",true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
            )
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); 
    }
}