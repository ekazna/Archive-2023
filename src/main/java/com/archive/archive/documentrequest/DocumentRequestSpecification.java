package com.archive.archive.documentrequest;

import com.archive.archive.documentrequest.dto.DocumentRequestFilter;
import org.springframework.data.jpa.domain.Specification;

public class DocumentRequestSpecification {

    public static Specification<DocumentRequest> withFilter(
            DocumentRequestFilter filter
    ) {
        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (filter.getRequestType() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("requestType"),
                                filter.getRequestType()
                        )
                );
            }

            if (filter.getRequestStatus() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("requestStatus"),
                                filter.getRequestStatus()
                        )
                );
            }

            if (filter.getDocumentId() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("doc").get("id"),
                                filter.getDocumentId()
                        )
                );
            }

            if (filter.getEmployeeId() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("employee").get("id"),
                                filter.getEmployeeId()
                        )
                );
            }

            return predicate;
        };
    }


    public static Specification<DocumentRequest> requestedBy(Integer employeeId){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(
                    root.get("employee").get("id"), employeeId
            );
    }

    public static Specification<DocumentRequest> hasId(Integer id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("id"),
                        id
                );
    }
}