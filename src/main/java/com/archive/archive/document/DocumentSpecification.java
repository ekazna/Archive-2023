package com.archive.archive.document;

import com.archive.archive.document.dto.DocumentFilter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class DocumentSpecification {

    public static Specification<Doc> accessibleTo(Integer accessLevel){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("accessLevel"),
                        accessLevel
                ));
    }

    public static Specification<Doc> hasId(Integer id){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("id"),
                        id
                ));
    }

    public static Specification<Doc> isActive(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        DocumentStatus.ACTIVE
                );
    }

    public static Specification<Doc> expiredAsOf(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("deletionDate"),
                        date
                );
    }

    public static Specification<Doc> withFilter(DocumentFilter filter) {

        return (root, query, criteriaBuilder) -> {

            var predicates = criteriaBuilder.conjunction();

            // Название документа
            if (filter.getKeyword() != null
                    && !filter.getKeyword().isBlank()) {

                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + filter.getKeyword().toLowerCase() + "%"
                        )
                );
            }

            // Отдел
            if (filter.getDepartmentId() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("department").get("id"),
                                filter.getDepartmentId()
                        )
                );
            }

            // Тип документа
            if (filter.getDocTypeId() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("docType").get("id"),
                                filter.getDocTypeId()
                        )
                );
            }

            // Клиент
            if (filter.getClientId() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("client").get("id"),
                                filter.getClientId()
                        )
                );
            }

            // Сотрудник, указанный в документе (ответственный)
            if (filter.getEmployeeId() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("docEmployee").get("id"),
                                filter.getEmployeeId()
                        )
                );
            }

            // Дата документа
            if (filter.getDocDate() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("docDate"),
                                filter.getDocDate()
                        )
                );
            }

            // Находится ли оригинал в архиве
            if (filter.getPresent() != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("present"),
                                filter.getPresent()
                        )
                );
            }

            return predicates;
        };
    }
}