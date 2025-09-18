package com.archive.archive.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee implements UserDetails{
    @Id
    @Column(name = "emp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "access_level")
    Integer accessLevel;

    @Column(name = "email", unique=true)
    String email;

    @Column(name = "login", unique=true)
    String login;

    @Column(name = "password")
    String password;

    @ManyToOne 
    @JoinColumn(name = "dept_id", nullable = false)
    Department department;

    @JsonIgnore
    @OneToMany(mappedBy = "docEmployee")
    List<Doc> docsAbout;

    @JsonIgnore
    @OneToMany(mappedBy = "fromEmployee")
    List<Doc> docsGiven;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    List<DocAction> docActions;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    List<Order> orders; 

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        if (this.department.getId() == 13){
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else{
            list.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return list;
    }

    @Override
    public String getUsername() {
        return this.login;
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
