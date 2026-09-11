package com.archive.archive.controllers.rest;


import com.archive.archive.dto.DocumentRequestCreateRequest;
import com.archive.archive.dto.DocumentRequestResponse;
import com.archive.archive.models.DocumentRequest;
import com.archive.archive.services.DocumentRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/document-requests")
public class DocumentRequestRestController {

    private final DocumentRequestService documentRequestService;

    public DocumentRequestRestController(DocumentRequestService documentRequestService){
        this.documentRequestService = documentRequestService;
    }


    @PostMapping
    public ResponseEntity<DocumentRequestResponse> createRequest(
            @Valid @RequestBody DocumentRequestCreateRequest request){
        DocumentRequest documentRequest =
                documentRequestService.createRequest(
                        request.requestType(),
                        request.documentId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(documentRequest));

    }


    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeCopyRequest(@PathVariable Integer id){
        documentRequestService.completeCopyRequest(id);
    }

    @PostMapping("/{id}/issue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issueOriginal(@PathVariable Integer id){
        documentRequestService.issueOriginal(id);
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnOriginal(@PathVariable Integer id){
        documentRequestService.returnOriginal(id);
    }



    private DocumentRequestResponse toResponse(DocumentRequest request){
        return new DocumentRequestResponse(
                request.getId(),
                request.getDoc().getId(),
                request.getDoc().getName(),
                request.getEmployee().getId(),
                request.getEmployee().getEmail(),
                request.getRequestType(),
                request.getRequestStatus(),
                request.getRequestDate()
        );
    }
}
