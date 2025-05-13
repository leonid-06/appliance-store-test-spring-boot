package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.self.Manufacturer;
import org.springframework.data.jpa.domain.Specification;

public class ManufacturerSpec {
    public static Specification<Manufacturer> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Manufacturer> hasCountry(String country) {
        return (root, query, cb) ->
                country == null || country.isBlank()
                        ? cb.conjunction()
                        : cb.equal(root.get("country"), country);
    }

    public static Specification<Manufacturer> isNotDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("deleted"));
    }

    public static Specification<Manufacturer> isDeleted() {
        return (root, query, cb) ->
                cb.isTrue(root.get("deleted"));
    }

}
