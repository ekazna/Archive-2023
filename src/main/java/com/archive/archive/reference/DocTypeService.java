package com.archive.archive.reference;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DocTypeService {

    private final DocTypeRepo docTypeRepo;

    public DocTypeService(DocTypeRepo docTypeRepo) {
        this.docTypeRepo = docTypeRepo;
    }

    public List<DocType> getAll(){
        return docTypeRepo.findAll();
    }

    public List<DocType> getAllSortedAsc(){
        return docTypeRepo.findAll(Sort.by("name"));
    }
}
