package com.archive.archive.audit;

import java.time.LocalDateTime;

import com.archive.archive.document.Doc;
import com.archive.archive.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actions")
@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    Doc doc;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    ActionStatus actionStatus;
}
