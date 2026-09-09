package com.archive.archive.controllers.rest;


import com.archive.archive.dto.CreateDocumentRequest;
import com.archive.archive.dto.DocumentFilter;
import com.archive.archive.dto.DocumentResponse;
import com.archive.archive.dto.UpdateDocumentRequest;
import com.archive.archive.models.Doc;
import com.archive.archive.services.DocService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/documents")
public class DocumentRestController {

    private final DocService docService;

    public DocumentRestController(DocService docService){
        this.docService = docService;
    }

    private DocumentResponse toResponse(Doc doc){
        return new DocumentResponse(
                doc.getId(),
                doc.getName(),
                doc.getDocDate(),
                doc.getAccessLevel()
        );
    }



    @GetMapping("/{id}")
    public DocumentResponse getDocument(@PathVariable Integer id){

        Doc doc = docService.getById(id);
        return toResponse(doc);
    }



    @GetMapping
    public Page<DocumentResponse> getDocuments(
            @ModelAttribute DocumentFilter filter, //вместо кучи @RequestParam
            Pageable pageable){
        Page<Doc> documents = docService.getAll(filter, pageable);

        return documents.map(this::toResponse);
    }


    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(
            @Valid @RequestBody CreateDocumentRequest request
            ){
        Doc doc = docService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(doc));
    }


    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDocumentRequest request
            ){
        Doc doc = docService.update(id, request);

        return toResponse(doc);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // 204 No content - Successful but NO Body
    public void deleteDocument(@PathVariable Integer id){
        docService.dispose(id);
    }
}
