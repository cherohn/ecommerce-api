package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.request.CreateCategoryRequest;
import com.cherohn.ecommerce_api.exception.ConflictException;
import com.cherohn.ecommerce_api.exception.ResourceNotFoundException;
import com.cherohn.ecommerce_api.model.Category;
import com.cherohn.ecommerce_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequest request) {
        if(categoryRepository.findByName(request.getName()).isPresent()) {
            throw new ConflictException("Ja existe uma categoria com este nome");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));
    }
}
