package com.onlinebookstore.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, S extends Record> {
    Specification<T> build(S searchParameters);
}
