package com.archive.archive.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "departments")
@Getter
@Setter
public class Department {
    @Id
    @Column(name = "dept_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", unique=true)
    String name;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "department")
    List<Doc> docs;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "department")
    List<Employee> employees;
}
