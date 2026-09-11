package com.archive.archive.documentrequest;

import com.archive.archive.documentrequest.dto.DocumentRequestResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentRequestMapper {

    public DocumentRequestResponse toResponse(
            DocumentRequest request
    ) {
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