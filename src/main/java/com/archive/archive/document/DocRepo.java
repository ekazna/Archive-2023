package com.archive.archive.document;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocRepo extends
        JpaRepository<Doc, Integer>,
        JpaSpecificationExecutor<Doc>{
}
