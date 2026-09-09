package com.archive.archive.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UpdateDocumentRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String folder;

    @NotNull
    private LocalDate docDate;

    @NotNull
    @Min(1)
    private Integer accessLevel;

    @NotNull
    private Integer docTypeId;

    @NotNull
    private Integer departmentId;

    @NotNull
    private Integer fromEmployeeId;

    private Integer clientId;

    private Integer docEmployeeId;

    private LocalDate deletionDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public LocalDate getDocDate() {
        return docDate;
    }

    public void setDocDate(LocalDate docDate) {
        this.docDate = docDate;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Integer getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(Integer docTypeId) {
        this.docTypeId = docTypeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getFromEmployeeId() {
        return fromEmployeeId;
    }

    public void setFromEmployeeId(Integer fromEmployeeId) {
        this.fromEmployeeId = fromEmployeeId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getDocEmployeeId() {
        return docEmployeeId;
    }

    public void setDocEmployeeId(Integer docEmployeeId) {
        this.docEmployeeId = docEmployeeId;
    }

    public LocalDate getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(LocalDate deletionDate) {
        this.deletionDate = deletionDate;
    }
}