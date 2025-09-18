package com.archive.archive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.archive.archive.models.DocType;

public interface DocTypeRepo extends JpaRepository<DocType, Integer>{
}
