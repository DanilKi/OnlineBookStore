package com.onlinebookstore.repository.book;

import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.SpecificationBuilder;
import com.onlinebookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book,
        BookSearchParametersDto> {
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_PRICE = "price";
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(KEY_TITLE)
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(KEY_AUTHOR)
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.isbn() != null) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(KEY_ISBN)
                    .getSpecification(new String[]{searchParameters.isbn()}));
        }
        if (searchParameters.priceFrom() != null || searchParameters.priceTo() != null) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(KEY_PRICE)
                    .getSpecification(new String[]{searchParameters.priceFrom(),
                                                    searchParameters.priceTo()}));
        }
        return spec;
    }
}
