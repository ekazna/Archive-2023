package com.archive.archive.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionStatusRepo extends JpaRepository<ActionStatus, Integer>{
    
}
