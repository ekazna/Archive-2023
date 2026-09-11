package com.archive.archive.repositories;


import java.util.List;

import com.archive.archive.models.DocumentRequest;
import com.archive.archive.models.RequestStatus;
import com.archive.archive.models.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequestRepo extends JpaRepository<DocumentRequest, Integer> {

    List<DocumentRequest> findByRequestTypeAndRequestStatus(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<DocumentRequest> findByRequestTypeAndRequestStatusOrderByDoc_Name(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<DocumentRequest> findByRequestTypeAndRequestStatusOrderByDoc_Folder(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<DocumentRequest> findByRequestTypeAndRequestStatusOrderByEmployee_Email(
            RequestType requestType,
            RequestStatus requestStatus
    );

    List<DocumentRequest> findByRequestTypeAndRequestStatusOrderByOrderDate(
            RequestType requestType,
            RequestStatus requestStatus
    );
}