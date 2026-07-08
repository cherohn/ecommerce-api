package com.cherohn.ecommerce_api.controller;

import com.cherohn.ecommerce_api.dto.request.CreateProductRequest;
import com.cherohn.ecommerce_api.dto.request.StockAdjustmentRequest;
import com.cherohn.ecommerce_api.dto.request.UpdateProductRequest;
import com.cherohn.ecommerce_api.dto.response.PageResponse;
import com.cherohn.ecommerce_api.dto.response.ProductResponse;
import com.cherohn.ecommerce_api.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Produtos")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.listProducts(name, categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deactivate(@PathVariable Long id){
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/add")
    public ResponseEntity<ProductResponse> addStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request){
        return ResponseEntity.ok(productService.addStock(id, request.getQuantity()));
    }

    @PatchMapping("/{id]/stock/remove")
    public ResponseEntity<ProductResponse> removeStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request){
        return ResponseEntity.ok(productService.removeStock(id, request.getQuantity()));
    }
}


