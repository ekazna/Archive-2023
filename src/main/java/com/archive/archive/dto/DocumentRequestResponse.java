package com.archive.archive.dto;

import com.archive.archive.models.RequestStatus;
import com.archive.archive.models.RequestType;

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
