package com.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.shoppingcart.CartItemDto;
import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    private static final String API_URL = "/cart";
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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/shoppingcarts/add-books-and-cartitems-to-db-tables.sql")
            );
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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/shoppingcarts/remove-books-and-cartitems-from-db-tables.sql")
            );
        }
    }

    @Test
    @DisplayName("""
            Add a new item to the shopping cart
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    @Sql(scripts = "classpath:database/shoppingcarts/remove-saved-cartitem-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addItemToShoppingCart_ValidCreateCartItemRequestDto_ReturnsValidShoppingCartDto()
            throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(3L);
        requestDto.setQuantity(1);
        String requestJson = objectMapper.writeValueAsString(requestDto);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, List.of(
                new CartItemDto(1L, 1L, "Sample Book 1", 1),
                new CartItemDto(2L, 2L, "Sample Book 2", 2))
        );

        MvcResult result = mockMvc.perform(post(API_URL)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertNotNull(actual);
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        actual.cartItems().sort(Comparator.comparingLong(CartItemDto::id));
        assertIterableEquals(expected.cartItems(), actual.cartItems());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "cartItems"));
    }

    @Test
    @DisplayName("""
            Get current user's shopping cart
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    void getShoppingCartByUserName_UserAuthenticated_ReturnsCorrectShoppingCartDto()
            throws Exception {
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, List.of());

        MvcResult result = mockMvc.perform(get(API_URL)).andExpect(status().isOk()).andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "cartItems"));
    }

    @Test
    @DisplayName("""
            Update cart item by id
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    @Sql(scripts = "classpath:database/shoppingcarts/restore-updated-cartitem-in-db-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemInShoppingCart_CartItemWithIdExists_ReturnsUpdatedShoppingCartDto()
            throws Exception {
        Long cartItemId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(2);
        String requestJson = objectMapper.writeValueAsString(requestDto);
        ShoppingCartDto expected = new ShoppingCartDto(2L, 2L, List.of(
                new CartItemDto(1L, 1L, "Sample Book 1", 2),
                new CartItemDto(2L, 2L, "Sample Book 2", 2))
        );

        MvcResult result = mockMvc.perform(put(API_URL + "/items/{cartItemId}", cartItemId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        assertNotNull(actual);
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        actual.cartItems().sort(Comparator.comparingLong(CartItemDto::id));
        assertIterableEquals(expected.cartItems(), actual.cartItems());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Delete cart item by id
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    @Sql(scripts = "classpath:database/shoppingcarts/save-new-cartitem-to-db-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteItemFromShoppingCart_CartItemWithIdAvailable_ReturnsNoContentStatus()
            throws Exception {
        Long cartItemId = 4L;

        mockMvc.perform(delete(API_URL + "/items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent());
    }
}
