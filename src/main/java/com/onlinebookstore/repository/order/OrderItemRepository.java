package com.onlinebookstore.repository.order;

import com.onlinebookstore.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"order", "order.user"})
    Optional<OrderItem> findByIdAndOrderIdAndOrderUserEmail(Long id, Long orderId, String email);
}
