package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.exception.InsufficientStockException;
import com.cherohn.ecommerce_api.exception.ResourceNotFoundException;
import com.cherohn.ecommerce_api.model.Category;
import com.cherohn.ecommerce_api.model.Product;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;
    private Product product;

    @BeforeEach
    void setUp() {
        Category category =
                Category.builder().id(1L).name("Eletrônicos").build();
        product = Product.builder()
                .id(1L)
                .name("Teclado Mecânico")
                .price(BigDecimal.valueOf(250.00))
                .stockQuantity(10)
                .active(true)
                .category(category)
                .build();
    }   // ← ESSA CHAVE ESTAVA FALTANDO AQUI

    @Nested
    @DisplayName("Testes de remoção de estoque")
    class RemoveStockTests {

        @Test
        @DisplayName("Deve lançar InsufficientStockException quando quantidade solicitada é maior que o estoque")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InsufficientStockException.class, () -> {
                productService.removeStock(1L, 20);
            });
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Deve decrementar estoque corretamente quando quantidade é válida")
        void deveDecrementarEstoqueQuandoQuantidadeValida() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
            productService.removeStock(1L, 4);
            assertEquals(6, product.getStockQuantity());
        }
    }

    @Nested
    @DisplayName("Testes de desativação de produto")
    class DeactivateTests {

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao desativar produto inexistente")
        void deveLancarExcecaoQuandoProdutoNaoExiste() {
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> {
                productService.deactivateProduct(99L);
            });
        }
    }

    @Nested
    @DisplayName("Testes de adição de estoque")
    class AddStockTests {

        @Test
        @DisplayName("Deve incrementar o campo stockQuantity corretamente")
        void deveIncrementarEstoqueCorretamente() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
            productService.addStock(1L, 15);
            assertEquals(25, product.getStockQuantity());
        }
    }
}
