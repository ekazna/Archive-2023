package com.archive.archive.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "order_type")
    Integer type;

    @Column(name = "order_date")
    LocalDate orderDate;

    @ManyToOne 
    @JoinColumn(name = "doc_id", nullable = false)
    Doc doc;
    
    @ManyToOne 
    @JoinColumn(name = "emp_id", nullable = false)
    Employee employee;



}
