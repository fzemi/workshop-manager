package com.fzemi.workshopmanager.part.entity;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class PartSpecification {

    private static String likePattern(String value) {
        return "%" + value.toLowerCase() + "%";
    }

    public static Specification<Part> filterPart(
            String partName,
            String serialNumber,
            String manufacturer
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("partName")),
                    StringUtils.isBlank(partName) ? likePattern("") : likePattern(partName));

            Predicate serialPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("serialNumber")),
                    StringUtils.isBlank(serialNumber) ? likePattern("") : likePattern(serialNumber));

            Predicate manufacturerPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("manufacturer")),
                    StringUtils.isBlank(manufacturer) ? likePattern("") : likePattern(manufacturer));

            return criteriaBuilder.and(namePredicate, serialPredicate, manufacturerPredicate);
        };

    }
}
