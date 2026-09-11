package com.archive.archive.document;


import com.archive.archive.document.dto.DocumentCreateRequest;
import com.archive.archive.document.dto.DocumentFilter;
import com.archive.archive.document.dto.DocumentResponse;
import com.archive.archive.document.dto.DocumentUpdateRequest;

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

    @GetMapping("/expired")
    public Page<DocumentResponse> getExpiredDocuments(Pageable pageable){
        return docService
                .getExpired(pageable)
                .map(this::toResponse);
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
            @Valid @RequestBody DocumentCreateRequest request
            ){
        Doc doc = docService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(doc));
    }


    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable Integer id,
            @Valid @RequestBody DocumentUpdateRequest request
            ){
        Doc doc = docService.update(id, request);

        return toResponse(doc);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // 204 No content - Successful but No body
    public void disposeDocument(@PathVariable Integer id){
        docService.dispose(id);
    }



}
