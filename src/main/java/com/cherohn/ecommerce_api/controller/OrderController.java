package com.cherohn.ecommerce_api.controller;

import com.cherohn.ecommerce_api.dto.request.CreateOrderRequest;
import com.cherohn.ecommerce_api.dto.request.UpdateOrderStatusRequest;
import com.cherohn.ecommerce_api.dto.response.OrderResponse;
import com.cherohn.ecommerce_api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request){
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request){
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }
}
