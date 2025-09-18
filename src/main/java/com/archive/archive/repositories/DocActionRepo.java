package com.archive.archive.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.archive.archive.models.DocAction;

public interface DocActionRepo extends JpaRepository<DocAction, Integer>{
    
    @Query(nativeQuery = true, value = "SELECT s.name, COUNT(a.id) FROM actions a FULL OUTER JOIN statuses s USING(status_id) GROUP BY s.name ORDER BY s.name")
    List<Object[]> statistics();
}
