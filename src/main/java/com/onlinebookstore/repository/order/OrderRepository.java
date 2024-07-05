package com.onlinebookstore.repository.order;

import com.onlinebookstore.model.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems"})
    Page<Order> findAllByUserEmail(String email, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems"})
    Optional<Order> findByIdAndUserEmail(Long id, String email);
}
