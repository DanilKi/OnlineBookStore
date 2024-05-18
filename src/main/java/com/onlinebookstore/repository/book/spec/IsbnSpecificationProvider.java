package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY_FIELD = "isbn";

    @Override
    public String getKey() {
        return KEY_FIELD;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(root.get(KEY_FIELD), "%" + params[0] + "%");
    }
}
