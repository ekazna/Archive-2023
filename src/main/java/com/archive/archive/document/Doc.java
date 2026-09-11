package com.archive.archive.document;

import java.time.LocalDate;

import com.archive.archive.models.Client;
import com.archive.archive.models.Department;
import com.archive.archive.models.DocType;
import com.archive.archive.models.Employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "docs")
@Getter
@Setter
@NoArgsConstructor
public class Doc {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "folder")
    String folder;

    @Column(name = "present")
    Boolean present;

    @Column(name = "date_added")
    LocalDate dateAdded;

    @Column(name = "deletion_date")
    LocalDate deletionDate;

    @Column(name = "doc_date")
    LocalDate docDate;

    @Column(name = "access_level")
    Integer accessLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @Version
    @Column(nullable = false)
    private Long version;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    DocType docType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dept_id", nullable = false)
    Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")  //поиск по имени для доков HR
    Employee docEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "given_by_id", nullable = false) // кто передал в архив документ
    Employee fromEmployee;




}
