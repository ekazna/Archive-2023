package com.archive.archive.document.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateDocumentRequest(

        @NotBlank
        String name,

        String folder,

        @NotNull
        LocalDate docDate,

        @NotNull
        @Min(1)
        Integer accessLevel,

        @NotNull
        Integer docTypeId,

        @NotNull
        Integer departmentId,

        @NotNull
        Integer fromEmployeeId,

        Integer clientId,

        Integer docEmployeeId,

        LocalDate deletionDate
) {
}