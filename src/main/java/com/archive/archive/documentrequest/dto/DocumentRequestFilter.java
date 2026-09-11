package com.archive.archive.documentrequest.dto;

import com.archive.archive.documentrequest.RequestStatus;
import com.archive.archive.documentrequest.RequestType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentRequestFilter {

    private RequestType requestType;
    private RequestStatus requestStatus;
    private Integer documentId;
    private Integer employeeId;
}