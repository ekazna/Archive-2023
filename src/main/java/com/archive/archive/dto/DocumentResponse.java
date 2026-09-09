package com.archive.archive.dto;

import java.time.LocalDate;

public record DocumentResponse(
        Integer id,
        String name,
        LocalDate docDate,
        Integer accessLevel
) {
}