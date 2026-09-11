package com.archive.archive.reference;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepo clientRepo;

    public ClientService(ClientRepo clientRepo){
        this.clientRepo = clientRepo;
    }


    public List<Client> getAll(){
        return clientRepo.findAll();
    }



    public List<Client> getAllSortedAsc(){
        return clientRepo.findAll(Sort.by("name"));
    }
}
