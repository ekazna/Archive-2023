package com.archive.archive.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.Client;

public interface ClientRepo extends JpaRepository<Client, Integer>{
   
}
