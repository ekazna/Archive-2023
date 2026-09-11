package com.archive.archive.dto;

import com.archive.archive.models.RequestType;
import jakarta.validation.constraints.NotNull;

public record DocumentRequestCreateRequest(
    @NotNull
    Integer documentId,
    @NotNull
    RequestType requestType){
}
