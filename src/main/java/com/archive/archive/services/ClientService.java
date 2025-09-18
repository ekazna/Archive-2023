package com.archive.archive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.models.Client;
import com.archive.archive.repositories.ClientRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ClientService {
    @Autowired
    ClientRepo clientRepo;



    public List<Client> getAll(){
        return clientRepo.findAll();
    }



    public List<Client> getAllSortedAsc(){
        return clientRepo.findAll(Sort.by("name"));
    }
}
