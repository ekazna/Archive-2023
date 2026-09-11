package com.archive.archive.documentrequest;


import java.util.Collection;
import java.util.Optional;

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

    boolean existsByDoc_IdAndRequestStatusIn(
            Integer documentId,
            Collection<RequestStatus> statuses
    );

    boolean existsByDoc_IdAndEmployee_IdAndRequestTypeAndRequestStatusIn(
            Integer documentId,
            Integer employeeId,
            RequestType requestType,
            Collection<RequestStatus> statuses
    );

}