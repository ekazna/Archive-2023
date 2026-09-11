package com.archive.archive.documentrequest.dto;

import com.archive.archive.documentrequest.RequestType;
import jakarta.validation.constraints.NotNull;

public record DocumentRequestCreateRequest(
    @NotNull
    Integer documentId,
    @NotNull
    RequestType requestType){
}
