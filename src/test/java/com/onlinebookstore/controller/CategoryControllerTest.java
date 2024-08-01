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
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
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
class CategoryControllerTest {
    private static final String API_URL = "/categories";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    @DisplayName("""
            Create a new category
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/remove-saved-category-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidCreateCategoryRequestDto_ReturnsValidCategoryDto() throws Exception {
        CategoryDto expected = new CategoryDto(3L, "Sample Category 3", null);
        CreateCategoryRequestDto requestDto =
                new CreateCategoryRequestDto("Sample Category 3", null);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(API_URL)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("""
            Get all available categories
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getAll_TwoCategoriesAvailable_ReturnListOfTwoCategoryDto() throws Exception {
        CategoryDto categoryDto1 = new CategoryDto(1L, "Sample Category 1", null);
        CategoryDto categoryDto2 = new CategoryDto(2L, "Sample Category 2", null);
        List<CategoryDto> expected = List.of(categoryDto1, categoryDto2);

        MvcResult result = mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> actual = Arrays.asList(objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto[].class
        ));

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Get an existing category by id
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getCategoryById_CategoryWithIdAvailable_ReturnsValidCategoryDto() throws Exception {
        CategoryDto expected = new CategoryDto(1L, "Sample Category 1", null);
        Long categoryId = expected.id();

        MvcResult result = mockMvc.perform(get(API_URL + "/{id}", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertNotNull(actual);
        assertEquals(categoryId, actual.id());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Update an existing category by id
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/restore-updated-category-state-in-db-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_CategoryWithIdAvailable_ReturnUpdatedCategoryDto() throws Exception {
        CreateCategoryRequestDto requestDto =
                new CreateCategoryRequestDto("Updated Category", null);
        CategoryDto expected = new CategoryDto(2L, requestDto.name(), requestDto.description());
        Long categoryId = expected.id();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(API_URL + "/{id}", categoryId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Delete an existing category by id
            """)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/categories/save-new-category-to-db-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategory_CategoryWithIdAvailable_ReturnsNoContentStatus() throws Exception {
        Long categoryId = 3L;

        mockMvc.perform(delete(API_URL + "/{id}", categoryId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            Get all books by category id
            """)
    @WithMockUser(username = "user", roles = {"USER"})
    void getBooksByCategoryId_CategoryWithIdAvailable_ReturnsListOfTwoBookDtoWithoutCategories()
            throws Exception {
        BookDtoWithoutCategoryIds bookDto1 = new BookDtoWithoutCategoryIds();
        bookDto1.setId(1L);
        bookDto1.setTitle("Sample Book 1");
        bookDto1.setAuthor("Author A");
        bookDto1.setIsbn("9781234567897");
        bookDto1.setPrice(BigDecimal.valueOf(19.99));
        BookDtoWithoutCategoryIds bookDto2 = new BookDtoWithoutCategoryIds();
        bookDto2.setId(2L);
        bookDto2.setTitle("Sample Book 2");
        bookDto2.setAuthor("Author B");
        bookDto2.setIsbn("9789876543210");
        bookDto2.setPrice(BigDecimal.valueOf(24.99));
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto1, bookDto2);
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(get(API_URL + "/{id}/books", categoryId))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actual = Arrays.asList(objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class
        ));

        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }
}
