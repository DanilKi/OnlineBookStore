package com.onlinebookstore.repository.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.model.Status;
import com.onlinebookstore.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("""
            Find all orders of a user by email
            """)
    @Sql(scripts = "classpath:database/orders/add-books-and-orders-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/orders/remove-books-and-orders-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByUserEmail_ValidUserEmail_ReturnsTwoOrders() {
        User user = new User();
        user.setId(2L);
        Order expected = new Order();
        expected.setId(1L);
        expected.setUser(user);
        expected.setOrderDate(LocalDateTime.of(2024,7, 3, 13, 2, 20));
        expected.setTotal(BigDecimal.valueOf(144.94));
        expected.setStatus(Status.SENT);
        expected.setShippingAddress("Kyiv, Main str, 10");
        String userEmail = "user1@gmail.com";

        Page<Order> orders = orderRepository.findAllByUserEmail(userEmail, PageRequest.of(0, 10));
        List<Order> actual = orders.getContent();

        assertEquals(2, actual.size());
        assertEquals(expected.getId(), actual.get(0).getId());
        assertEquals(expected.getUser().getId(), actual.get(0).getUser().getId());
        assertEquals(expected.getOrderDate(), actual.get(0).getOrderDate());
        assertEquals(expected.getTotal(), actual.get(0).getTotal());
        assertEquals(expected.getStatus(), actual.get(0).getStatus());
        assertEquals(expected.getShippingAddress(), actual.get(0).getShippingAddress());
        assertEquals(2, actual.get(0).getOrderItems().size());
    }

    @Test
    @DisplayName("""
            Find order of a user by id and user's email
            """)
    @Sql(scripts = "classpath:database/orders/add-books-and-orders-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/orders/remove-books-and-orders-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdAndUserEmail_OrderWithIdExistsValidEmail_ReturnsOneOrder() {
        User user = new User();
        user.setId(2L);
        Long orderId = 1L;
        Order expected = new Order();
        expected.setId(orderId);
        expected.setUser(user);
        expected.setOrderDate(LocalDateTime.of(2024,7, 3, 13, 2, 20));
        expected.setTotal(BigDecimal.valueOf(144.94));
        expected.setStatus(Status.SENT);
        expected.setShippingAddress("Kyiv, Main str, 10");
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(expected);
        orderItem1.setBook(new Book(1L));
        orderItem1.setQuantity(1);
        orderItem1.setPrice(BigDecimal.valueOf(19.99));
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(expected);
        orderItem2.setBook(new Book(2L));
        orderItem2.setQuantity(5);
        orderItem2.setPrice(BigDecimal.valueOf(124.95));
        expected.setOrderItems(Set.of(orderItem1, orderItem2));
        String userEmail = "user1@gmail.com";

        Order actual = orderRepository.findByIdAndUserEmail(orderId, userEmail).orElseThrow();

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "user", "orderItems"));
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(2, actual.getOrderItems().size());
    }
}
