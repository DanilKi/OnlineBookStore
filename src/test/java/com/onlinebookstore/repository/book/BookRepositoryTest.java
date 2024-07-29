package com.onlinebookstore.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.onlinebookstore.model.Book;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books by category when category with given id exists in DB
            """)
    @Sql(scripts = "classpath:database/books/add-books-and-categories-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-categories-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_CategoryWithIdExists_ReturnsTwoBooks() {
        Long categoryId = 1L;
        Book expected = new Book();
        expected.setId(1L);
        expected.setTitle("Sample Book 1");
        expected.setAuthor("Author A");
        expected.setIsbn("9781234567897");
        expected.setPrice(BigDecimal.valueOf(19.99));

        Page<Book> books = bookRepository.findAllByCategoriesId(PageRequest.of(0, 10), categoryId);
        List<Book> actual = books.getContent();

        assertEquals(2, actual.size());
        assertEquals(expected.getId(), actual.get(0).getId());
        assertEquals(expected.getTitle(), actual.get(0).getTitle());
        assertEquals(expected.getAuthor(), actual.get(0).getAuthor());
        assertEquals(expected.getIsbn(), actual.get(0).getIsbn());
        assertEquals(expected.getPrice(), actual.get(0).getPrice());
    }

    @Test
    @DisplayName("""
            Find no books by category when category with given id does not exists in DB
            """)
    @Sql(scripts = "classpath:database/books/add-books-and-categories-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books-and-categories-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_CategoryWithIdNotExists_ReturnsEmptyList() {
        Long categoryId = 3L;

        Page<Book> books = bookRepository.findAllByCategoriesId(PageRequest.of(0, 10), categoryId);
        List<Book> actual = books.getContent();

        assertTrue(actual.isEmpty());
    }
}
