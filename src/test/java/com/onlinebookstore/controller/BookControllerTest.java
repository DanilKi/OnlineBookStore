package com.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final String API_URL = "/books";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private BookDto bookDto;
    private CreateBookRequestDto requestDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/books/add-books-and-categories-to-db-tables.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                        "database/books/remove-books-and-categories-from-db-tables.sql")
            );
        }
    }

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Sample Book 1");
        bookDto.setAuthor("Author A");
        bookDto.setIsbn("9781234567897");
        bookDto.setPrice(BigDecimal.valueOf(19.99));
        bookDto.setCategoryIds(List.of(1L));

        requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sample Book 4");
        requestDto.setAuthor("Author D");
        requestDto.setIsbn("9783161484100");
        requestDto.setPrice(BigDecimal.valueOf(9.99));
        requestDto.setCategoryIds(List.of(2L));
    }

    @Test
    @DisplayName("""
            Get all available books
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getAll_ThreeBooksAvailable_ReturnsListOfThreeBookDto() throws Exception {
        BookDto expected = bookDto;

        MvcResult result = mockMvc.perform(get(API_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = Arrays.asList(
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class)
        );

        assertFalse(actual.isEmpty());
        assertEquals(3, actual.size());
        assertEquals(expected, actual.get(0));
        EqualsBuilder.reflectionEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("""
            Get an existing book by id
            """)
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void getBookById_BookWithIdAvailable_ReturnsValidBookDto() throws Exception {
        BookDto expected = bookDto;
        Long bookId = expected.getId();

        MvcResult result = mockMvc.perform(get(API_URL + "/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Get non-existing book by id
            """)
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void getBookById_BookWithIdNotAvailable_ThrowsException() throws Exception {
        Long bookId = 100L;

        MvcResult result = mockMvc.perform(get(API_URL + "/{id}", bookId))
                .andExpect(status().isNotFound())
                .andReturn();
        Exception actual = result.getResolvedException();

        assertTrue(actual instanceof EntityNotFoundException);
    }

    @Test
    @DisplayName("""
            Create a new book
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/remove-saved-book-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidCreateBookRequestDto_ReturnsValidBookDto() throws Exception {
        BookDto expected = bookDto;
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setCategoryIds(requestDto.getCategoryIds());
        String requestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(API_URL)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("""
            Update an existing book by id
            """)
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database/books/restore-updated-book-state-in-db-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_BookWithIdAvailable_ReturnsUpdatedBookDto() throws Exception {
        BookDto expected = bookDto;
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());
        expected.setCategoryIds(requestDto.getCategoryIds());
        Long bookId = expected.getId();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(API_URL + "/{id}", bookId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Update a non-existing book by id
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    void updateBook_BookWithIdNotAvailable_ThrowsException() throws Exception {
        Long bookId = 100L;
        String requestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(API_URL + "/{id}", bookId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Exception actual = result.getResolvedException();

        assertTrue(actual instanceof EntityNotFoundException);
    }

    @Test
    @DisplayName("""
            Delete an existing book by id
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/books/restore-deleted-book-state-in-db-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_BookWithIdAvailable_ReturnsNoContentStatus() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete(API_URL + "/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            Search for books with specified parameters
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void searchBooks_ValidParametersBookAvailable_ReturnsFoundBookDtoInList() throws Exception {
        List<BookDto> expected = List.of(bookDto);

        MvcResult result = mockMvc.perform(get(API_URL + "/search?authors=Author A&categories=1"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = Arrays.asList(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class
        ));

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0)));
    }
}
