package com.archive.archive.controllers.rest;


import com.archive.archive.dto.DocumentResponse;
import com.archive.archive.models.Doc;
import com.archive.archive.services.DocService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentRestController {

    private final DocService docService;

    public DocumentRestController(DocService docService){
        this.docService = docService;
    }


    @GetMapping("/{id}")
    public DocumentResponse getDocument(@PathVariable Integer id){

        Doc doc = docService.getById(id);
        return toResponse(doc);
    }

    private DocumentResponse toResponse(Doc doc){
        return new DocumentResponse(
                doc.getId(),
                doc.getName(),
                doc.getDocDate(),
                doc.getAccessLevel()
        );
    }


    @GetMapping
    public Page<DocumentResponse> getDocuments(Pageable pageable){
        Page<Doc> documents = docService.getAll(pageable);

        return documents.map(this::toResponse);
    }
}
