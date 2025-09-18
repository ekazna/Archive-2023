package com.archive.archive.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actions")
@Data
@NoArgsConstructor
public class DocAction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "date")
    LocalDateTime actionDate;

    @Column(name = "doc_name")
    String docName;

    @ManyToOne 
    @JoinColumn(name = "doc_id", nullable = true)
    Doc doc;
    
    @ManyToOne 
    @JoinColumn(name = "emp_id", nullable = false)
    Employee employee;

    @ManyToOne 
    @JoinColumn(name = "status_id", nullable = false)
    ActionStatus actionStatus;
}
