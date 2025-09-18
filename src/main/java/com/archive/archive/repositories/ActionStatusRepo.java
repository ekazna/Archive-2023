package com.archive.archive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.ActionStatus;

public interface ActionStatusRepo extends JpaRepository<ActionStatus, Integer>{
    
}
