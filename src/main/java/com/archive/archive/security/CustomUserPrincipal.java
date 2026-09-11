package com.archive.archive.security;

import com.archive.archive.models.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserPrincipal implements UserDetails {

    private final Integer employeeId;
    private final String login;
    private final String password;
    private final Role role;

    public CustomUserPrincipal(
            Integer employeeId,
            String login,
            String password,
            Role role
    ) {
        this.employeeId = employeeId;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}