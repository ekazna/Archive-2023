package com.archive.archive.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;



public class TestModelSpecification implements Specification<Doc> {

    private TestModel criteria;

    public TestModelSpecification(TestModel criteria) {
        this.criteria = criteria;
    }


    public Predicate toPredicate(Root<Doc> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

        Predicate returnPred = cb.disjunction();

        List<Predicate> patientLevelPredicates = new ArrayList<Predicate>();

        
        if (criteria.getKeyword() != null) patientLevelPredicates.add(cb.like(root.get("name"), "%"+criteria.getKeyword()+"%"));
        
        if (criteria.getDocDate() != null) patientLevelPredicates.add(cb.equal(root.get("docDate"), criteria.getDocDate()));
        
        Join<Doc, Department> depDocs = root.join("department");
        if (criteria.getDeptId() != null) patientLevelPredicates.add(cb.equal(depDocs.get("id"), criteria.getDeptId()));
        
        Join<Doc, DocType> typeDocs = root.join("docType");
        if (criteria.getDocTypeId() != null) patientLevelPredicates.add(cb.equal(typeDocs.get("id"), criteria.getDocTypeId()));
        
        Join<Doc, Client> clientDocs = root.join("client", JoinType.LEFT);
        if (criteria.getClientId() != null) patientLevelPredicates.add(cb.equal(clientDocs.get("id"), criteria.getClientId()));
        
        Join<Doc, Employee> empDocs = root.join("docEmployee", JoinType.LEFT);
        if (criteria.getEmployeeId() != null) patientLevelPredicates.add(cb.equal(empDocs.get("id"), criteria.getEmployeeId()));
        
        if (criteria.getPresent() != null) patientLevelPredicates.add(cb.equal(root.get("present"), criteria.getPresent()));

        if (criteria.getAccessLevel() != null) patientLevelPredicates.add(cb.lessThanOrEqualTo(root.get("accessLevel"), criteria.getAccessLevel()));

        returnPred = cb.and(patientLevelPredicates.toArray(new Predicate[]{}));
        return returnPred;
    }
}


