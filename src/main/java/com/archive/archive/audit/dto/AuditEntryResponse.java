package com.archive.archive.audit.dto;

import java.time.LocalDateTime;

public record AuditEntryResponse(
        Integer id,
        LocalDateTime actionDate,
        Integer documentId,
        String documentName,
        Integer employeeId,
        String employeeLogin,
        String action
) {
}