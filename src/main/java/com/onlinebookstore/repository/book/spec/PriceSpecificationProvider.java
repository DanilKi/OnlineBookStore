package com.onlinebookstore.repository.book.spec;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationProvider;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY_FIELD = "price";

    @Override
    public String getKey() {
        return KEY_FIELD;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            String priceFrom = params[0];
            String priceTo = params[1];
            Predicate predicate = criteriaBuilder.conjunction();
            if (priceFrom != null && !priceFrom.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root.get(KEY_FIELD),
                        new BigDecimal(priceFrom)));
            }
            if (priceTo != null && !priceTo.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root.get(KEY_FIELD),
                        new BigDecimal(priceTo)));
            }
            return predicate;
        };
    }
}
