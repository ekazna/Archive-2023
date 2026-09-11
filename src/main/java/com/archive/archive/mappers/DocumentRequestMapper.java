package com.archive.archive.mappers;

import com.archive.archive.dto.DocumentRequestResponse;
import com.archive.archive.models.DocumentRequest;
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