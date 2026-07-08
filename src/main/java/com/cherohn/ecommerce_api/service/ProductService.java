package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.request.CreateProductRequest;
import com.cherohn.ecommerce_api.dto.request.UpdateProductRequest;
import com.cherohn.ecommerce_api.dto.response.PageResponse;
import com.cherohn.ecommerce_api.dto.response.ProductResponse;
import com.cherohn.ecommerce_api.exception.InsufficientStockException;
import com.cherohn.ecommerce_api.exception.ResourceNotFoundException;
import com.cherohn.ecommerce_api.model.Category;
import com.cherohn.ecommerce_api.model.Product;
import com.cherohn.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryService.getById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .build();

        Product saved =  productRepository.save(product);
        return toResponse(saved);
    }

    public PageResponse<ProductResponse> listProducts(String name, Long categoryId, Pageable pageable) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = categoryId != null;

        Page<Product> products;

        if(hasName && hasCategory) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndActiveTrue(name, categoryId, pageable);
        } else if(hasName) {
            products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
        } else if(hasCategory) {
            products = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        } else {
            products = productRepository.findByActiveTrue(pageable);
        }

        return PageResponse.of(products.map(this::toResponse));
    }

    public ProductResponse getById(Long id){
        Product product = findProductOrThrow(id);
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {

        Product product = findProductOrThrow(id);

        if(request.getName() != null) {
            product.setName(request.getName());
        }
        if(request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if(request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if(request.getCategory_id() != null) {
            product.setCategory(categoryService.getById(request.getCategory_id()));
        }

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    public void deactivateProduct(Long id){
        Product product = findProductOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductResponse addStock(Long id, int quantity) {
        Product product = findProductOrThrow(id);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    public ProductResponse removeStock(Long id, int quantity) {
        Product product = findProductOrThrow(id);

        if(product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .build();
    }

}
