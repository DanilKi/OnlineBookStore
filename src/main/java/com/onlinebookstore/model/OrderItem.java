package com.onlinebookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Book book;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal price;
}
