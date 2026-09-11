package com.archive.archive.documentrequest;


import com.archive.archive.documentrequest.dto.DocumentRequestCreateRequest;
import com.archive.archive.documentrequest.dto.DocumentRequestFilter;
import com.archive.archive.documentrequest.dto.DocumentRequestResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        DocumentRequestResponse response =
                documentRequestService.createRequest(
                        request.requestType(),
                        request.documentId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

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



    @GetMapping
    public Page<DocumentRequestResponse> getRequests(
            @ModelAttribute DocumentRequestFilter filter, Pageable pageable
            ){
        return documentRequestService.getAll(filter, pageable);
    }


    @GetMapping("/{id}")
    public DocumentRequestResponse getRequest(
            @PathVariable Integer id
    ){
        return documentRequestService.getById(id);
    }
}
