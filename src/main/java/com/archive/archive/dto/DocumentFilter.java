package com.archive.archive.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentFilter {

    private String keyword;
    private Integer departmentId;
    private Integer docTypeId;
    private Integer clientId;
    private Integer employeeId;
    private LocalDate docDate;
    private Boolean present;


}
