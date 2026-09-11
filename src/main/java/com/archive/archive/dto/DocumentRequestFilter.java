package com.archive.archive.dto;

import com.archive.archive.models.RequestStatus;
import com.archive.archive.models.RequestType;
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