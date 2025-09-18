package com.archive.archive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.Department;

public interface DepartmentRepo extends JpaRepository<Department, Integer> {
    
}
