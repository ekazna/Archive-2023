package com.archive.archive.models;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestModel {
    String keyword;
    Integer deptId;
    Integer docTypeId;
    Integer clientId;
    Integer employeeId;
    LocalDate docDate;
    Boolean present;
    Integer accessLevel;

    String sortBy;
    Integer sortingType;

    public TestModel(){
        this.sortBy = "name";
        this.sortingType = 0;
        this.accessLevel = 1;
    }
    
}
