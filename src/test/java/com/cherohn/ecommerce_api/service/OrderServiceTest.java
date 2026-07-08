package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.request.CreateOrderRequest;
import com.cherohn.ecommerce_api.dto.request.OrderItemRequest;
import com.cherohn.ecommerce_api.exception.InsufficientStockException;
import com.cherohn.ecommerce_api.exception.InvalidOrderStatusTransitionException;
import com.cherohn.ecommerce_api.model.*;
import com.cherohn.ecommerce_api.repository.OrderRepository;
import com.cherohn.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private OrderService orderService;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer =
                Customer.builder().id(1L).name("Matheus").email("matheus@email.com").build();
        product = Product.builder()
                .id(1L)
                .name("Mouse Gamer")
                .price(BigDecimal.valueOf(150.00))
                .stockQuantity(10)
                .build();
    }

    @Nested
    @DisplayName("Testes de criação de pedido")
    class CreateOrderTests {

        @Test
        @DisplayName("Deve criar pedido e decrementar estoque quando há quantidade suficiente")
        void deveCriarPedidoEDecrementarEstoque() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .customerId(1L)
                    .items(List.of(OrderItemRequest.builder().productId(1L).quantity(3).build()))
                    .build();
            when(customerService.getById(1L)).thenReturn(customer);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            orderService.createOrder(request);
            assertEquals(7, product.getStockQuantity());
            verify(orderRepository, times(1)).save(any(Order.class));

        }

        @Test
        @DisplayName("Deve lançar InsufficientStockException quando quantidade excede o estoque")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {

            CreateOrderRequest request = CreateOrderRequest.builder()
                    .customerId(1L)
                    .items(List.of(OrderItemRequest.builder().productId(1L).quantity(999).build()))
                    .build();
            when(customerService.getById(1L)).thenReturn(customer);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
            verify(orderRepository, never()).save(any(Order.class));

        }
    }

    @Nested
    @DisplayName("Testes de transição de status")
    class StatusTransitionTests {

        @Test
        @DisplayName("Deve lançar InvalidOrderStatusTransitionException ao tentar transição inválida")
        void deveLancarExcecaoParaTransicaoInvalida() {
            Order order = Order.builder()
                    .id(1L)
                    .status(OrderStatus.DELIVERED)
                    .customer(customer)
                    .build();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            assertThrows(InvalidOrderStatusTransitionException.class, () -> {orderService.updateOrderStatus(1L, OrderStatus.PENDING);
            });

        }

        @Test
        @DisplayName("Deve devolver estoque ao cancelar pedido")
        void deveDevolverEstoqueAoCancelarPedido() {

            OrderItem item = OrderItem.builder().product(product).quantity(5).build();
            Order order = Order.builder()
                    .id(1L)
                    .status(OrderStatus.PENDING)
                    .customer(customer)
                    .items(List.of(item))
                    .build();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);
            assertEquals(15, product.getStockQuantity());

        }
    }
}
