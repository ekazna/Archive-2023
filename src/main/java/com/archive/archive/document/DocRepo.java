package com.archive.archive.document;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DocRepo extends
        JpaRepository<Doc, Integer>,
        JpaSpecificationExecutor<Doc>{

    @EntityGraph(attributePaths = {
            "department",
            "docType",
            "client",
            "fromEmployee",
            "docEmployee"
    })
    @Override
    Optional<Doc> findOne(Specification<Doc> spec);
}
