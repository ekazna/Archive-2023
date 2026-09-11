package com.archive.archive.documentrequest.dto;

import com.archive.archive.documentrequest.RequestStatus;
import com.archive.archive.documentrequest.RequestType;

import java.time.LocalDate;

public record DocumentRequestResponse(
    Integer id,
    Integer documentId,
    String documentName,
    Integer employeeId,
    String employeeEmail,
    RequestType requestType,
    RequestStatus requestStatus,
    LocalDate requestDate
    ){


}
