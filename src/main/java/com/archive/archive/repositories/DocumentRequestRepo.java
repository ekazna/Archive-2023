package com.archive.archive.repositories;


import java.util.List;
import java.util.Optional;

import com.archive.archive.models.DocumentRequest;
import com.archive.archive.models.RequestStatus;
import com.archive.archive.models.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRequestRepo extends
        JpaRepository<DocumentRequest, Integer>,
        JpaSpecificationExecutor<DocumentRequest> {


    @Override
    @EntityGraph(attributePaths = {"doc", "employee"})
    Page<DocumentRequest> findAll(
            Specification<DocumentRequest> spec,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {"doc", "employee"})
    Optional<DocumentRequest> findOne(
            Specification<DocumentRequest> spec
    );

}