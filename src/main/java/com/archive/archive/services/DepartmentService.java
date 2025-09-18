package com.archive.archive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.models.Department;
import com.archive.archive.repositories.DepartmentRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DepartmentService {
    @Autowired
    DepartmentRepo departmentRepo;

    public List<Department> getAll(){
        return departmentRepo.findAll();
    }
    
    public List<Department> getAllSortedAsc(){
        return departmentRepo.findAll(Sort.by("name"));
    }

    public Department findById(Integer id){
        return departmentRepo.findById(id).get();
    }

}
