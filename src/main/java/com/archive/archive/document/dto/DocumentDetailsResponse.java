package com.archive.archive.document.dto;

import com.archive.archive.document.DocumentStatus;

import java.time.LocalDate;

public record DocumentDetailsResponse(
        Integer id,
        String name,
        String folder,
        LocalDate docDate,
        LocalDate dateAdded,
        LocalDate deletionDate,
        Integer accessLevel,
        Boolean present,
        DocumentStatus status,

        Integer departmentId,
        String departmentName,

        Integer documentTypeId,
        String documentTypeName,

        Integer clientId,
        String clientName,

        Integer fromEmployeeId,
        String fromEmployeeLogin,

        Integer documentEmployeeId,
        String documentEmployeeLogin
) {
}