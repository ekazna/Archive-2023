package com.archive.archive.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.Order;

public interface OrderRepo extends JpaRepository<Order, Integer>{

    List<Order> findByType(Integer typeId);

    List<Order> findByTypeOrderByDoc_Name(Integer typeId);

    List<Order> findByTypeOrderByDoc_Folder(Integer typeId);

    List<Order> findByTypeOrderByEmployee_Email(Integer typeId);

    List<Order> findByTypeOrderByOrderDate(Integer typeId);
    
    
}
