package com.onlinebookstore.repository.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository cartRepository;

    @Test
    @DisplayName("""
            Find shopping cart of a user by email
            """)
    @Sql(scripts = "classpath:database/shoppingcarts/add-books-and-cartitems-to-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            "classpath:database/shoppingcarts/remove-books-and-cartitems-from-db-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserEmail_ValidUserEmail_ReturnsCorrectShoppingCart() {
        User user = new User();
        user.setId(2L);
        ShoppingCart expected = new ShoppingCart();
        expected.setId(2L);
        expected.setUser(user);
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setShoppingCart(expected);
        cartItem1.setBook(new Book(1L));
        cartItem1.setQuantity(1);
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setShoppingCart(expected);
        cartItem2.setBook(new Book(2L));
        cartItem2.setQuantity(2);
        expected.setCartItems(Set.of(cartItem1, cartItem2));
        String userEmail = "user1@gmail.com";

        ShoppingCart actual = cartRepository.findByUserEmail(userEmail).orElseThrow();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(2, actual.getCartItems().size());
    }
}
