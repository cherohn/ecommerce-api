package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.request.CreateOrderRequest;
import com.cherohn.ecommerce_api.dto.request.OrderItemRequest;
import com.cherohn.ecommerce_api.dto.response.OrderItemResponse;
import com.cherohn.ecommerce_api.dto.response.OrderResponse;
import com.cherohn.ecommerce_api.exception.InsufficientStockException;
import com.cherohn.ecommerce_api.exception.InvalidOrderStatusTransitionException;
import com.cherohn.ecommerce_api.exception.ResourceNotFoundException;
import com.cherohn.ecommerce_api.model.*;
import com.cherohn.ecommerce_api.repository.OrderRepository;
import com.cherohn.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerService customerService;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS
            = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerService.getById(request.getCustomerId());
        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product =
                    productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + itemRequest.getProductId()));
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() -
                    itemRequest.getQuantity());
            productRepository.save(product);
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            order.addItem(item);
            total = total.add(subtotal);
        }
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
        return toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + orderId));
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());
        if (!allowed.contains(newStatus)) {
            throw new InvalidOrderStatusTransitionException(
                    "Não é possível mudar o status de " + order.getStatus() + " para " + newStatus);
        }
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com id: " + id));
        return toResponse(order);
    }
    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() +
                    item.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .customerName(order.getCustomer().getName())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
