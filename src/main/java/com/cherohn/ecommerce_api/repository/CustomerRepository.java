package com.cherohn.ecommerce_api.repository;

import com.cherohn.ecommerce_api.dto.response.RevenueAggregate;
import com.cherohn.ecommerce_api.dto.response.TopProductResponse;
import com.cherohn.ecommerce_api.model.Order;
import com.cherohn.ecommerce_api.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.time.LocalDateTime;

public interface CustomerRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("""
        SELECT new com.cherohn.ecommerce_api.dto.response.TopProductResponse(p.id, p.name, SUM(oi.quantity), SUM(oi.subtotal))
                FROM OrderItem oi
                JOIN oi.product p
                GROUP BY p.id, p.name
                ORDER BY SUM(oi.quantity) DESC
        """)
    List<TopProductResponse> findTopSellingProducts(Pageable pageable);

    @Query("""
            SELECT new com.cherohn.ecommerce_api.dto.response.RevenueAggregate(COUNT(o), COALESCE(SUM(o.totalAmount), 0))
                FROM Order o
                WHERE o.status = :status
                AND o.createdAt BETWEEN :startDateTime AND :endDateTime
            """)
    RevenueAggregate getRevenueAggregate(
            @Param("status") OrderStatus status,
            @Param("starDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
