package com.archive.archive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.models.DocType;
import com.archive.archive.repositories.DocTypeRepo;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class DocTypeService {
    @Autowired
    DocTypeRepo docTypeRepo;


    public List<DocType> getAll(){
        return docTypeRepo.findAll();
    }

    public List<DocType> getAllSortedAsc(){
        return docTypeRepo.findAll(Sort.by("name"));
    }
}
