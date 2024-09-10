package com.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import com.onlinebookstore.model.Status;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
class OrderControllerTest {
    private static final String API_URL = "/orders";
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
                    "database/orders/add-books-and-orders-to-db-tables.sql")
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
                    "database/orders/remove-books-and-orders-from-db-tables.sql")
            );
        }
    }

    @Test
    @DisplayName("""
            Create a new order
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    @Sql(scripts = "classpath:database/orders/add-new-cartitem-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/orders/remove-saved-order-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createOrder_ValidCreateOrderRequestDto_ReturnsValidOrderDto() throws Exception {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto("Kyiv, Main str, 10");
        String requestJson = objectMapper.writeValueAsString(requestDto);
        OrderDto expected = new OrderDto(
                3L,
                2L,
                LocalDateTime.now(),
                BigDecimal.valueOf(19.99),
                Status.PENDING,
                List.of(new OrderItemDto(4L, 1L, 1))
        );

        MvcResult result = mockMvc.perform(post(API_URL)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );

        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "orderDate"));
        assertTrue(expected.orderDate().isBefore(actual.orderDate()));
        assertTrue(expected.orderDate().until(actual.orderDate(), ChronoUnit.SECONDS) < 1L);
    }

    @Test
    @DisplayName("""
            Get current user's orders
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    void getAllOrdersByUser_TwoOrdersAvailable_ReturnsListOfTwoOrderDto() throws Exception {
        List<OrderDto> expected = List.of(
                new OrderDto(
                        1L,
                        2L,
                        LocalDateTime.of(2024, 7, 3, 13, 2, 20),
                        BigDecimal.valueOf(144.94),
                        Status.SENT,
                        List.of(new OrderItemDto(1L, 1L, 1), new OrderItemDto(2L, 2L, 5))
                ),
                new OrderDto(
                        2L,
                        2L,
                        LocalDateTime.of(2024, 7, 4, 12, 3, 5),
                        BigDecimal.valueOf(29.99),
                        Status.PENDING,
                        List.of(new OrderItemDto(3L, 3L, 1))
                )
        );

        MvcResult result = mockMvc.perform(get(API_URL)).andExpect(status().isOk()).andReturn();
        List<OrderDto> actual = Arrays.asList(
                objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto[].class)
        );

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(expected.size(), actual.size());
        assertIterableEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0)));
    }

    @Test
    @DisplayName("""
            Get all items in the order
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    void getAllItemsInOrder_TwoOrderItemsAvailable_ReturnsListOfTwoOrderItemDto()
            throws Exception {
        Long orderId = 1L;
        List<OrderItemDto> expected = List.of(
                new OrderItemDto(1L, 1L, 1),
                new OrderItemDto(2L, 2L, 5)
        );

        MvcResult result = mockMvc.perform(get(API_URL + "/{orderId}/items", orderId))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderItemDto> actual = Arrays.asList(
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        OrderItemDto[].class)
        );

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(expected.size(), actual.size());
        actual.sort(Comparator.comparingLong(OrderItemDto::id));
        assertIterableEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0)));
    }

    @Test
    @DisplayName("""
            Get existing item from the order by id
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    void getItemInOrderById_OrderContainsItem_ReturnsValidOrderItemDto() throws Exception {
        Long orderId = 1L;
        Long id = 2L;
        OrderItemDto expected = new OrderItemDto(2L, 2L, 5);

        MvcResult result = mockMvc.perform(get(API_URL + "/{orderId}/items/{id}", orderId, id))
                .andExpect(status().isOk())
                .andReturn();
        OrderItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderItemDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("""
            Get non-existing item from the order by id
            """)
    @WithMockUser(username = "user1@gmail.com", roles = {"USER"})
    void getItemInOrderById_OrderDoesNotContainItem_ThrowsException() throws Exception {
        Long orderId = 1L;
        Long id = 3L;

        MvcResult result = mockMvc.perform(get(API_URL + "/{orderId}/items/{id}", orderId, id))
                .andExpect(status().isNotFound())
                .andReturn();
        Exception actual = result.getResolvedException();

        assertTrue(actual instanceof EntityNotFoundException);
    }

    @Test
    @DisplayName("""
            Update order by id
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/orders/restore-updated-order-state-in-db-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrder_OrderWithIdExists_ReturnsUpdatedOrderDto() throws Exception {
        Long id = 2L;
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto(Status.SENT);
        String requestJson = objectMapper.writeValueAsString(requestDto);
        OrderDto expected = new OrderDto(
                2L,
                2L,
                LocalDateTime.of(2024, 7, 4, 12, 3, 5),
                BigDecimal.valueOf(29.99),
                Status.SENT,
                List.of(new OrderItemDto(3L, 3L, 1))
        );

        MvcResult result = mockMvc.perform(patch(API_URL + "/{id}", id)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }
}
