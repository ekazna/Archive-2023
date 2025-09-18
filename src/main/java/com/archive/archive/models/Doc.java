package com.archive.archive.models;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docs")
@Data
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

    @ManyToOne 
    @JoinColumn(name = "type_id", nullable = false)
    DocType docType;

    @ManyToOne 
    @JoinColumn(name = "dept_id", nullable = false)
    Department department;

    @ManyToOne 
    @JoinColumn(name = "client_id", nullable = true)
    Client client;

    @ManyToOne 
    @JoinColumn(name = "emp_id", nullable = true) //поиск по имени для доков HR
    Employee docEmployee;

    @ManyToOne 
    @JoinColumn(name = "given_by_id", nullable = false) // кто передал в архив документ
    Employee fromEmployee;


    @JsonIgnore
    @OneToMany(mappedBy = "doc")
    List<DocAction> docActions; 

    @JsonIgnore
    @OneToMany(mappedBy = "doc")
    List<Order> orders; 


}
