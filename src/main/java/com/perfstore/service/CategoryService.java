package com.perfstore.service;

import com.perfstore.domain.Category;
import com.perfstore.dto.CategoryDto;
import com.perfstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryDto.Response createCategory(CategoryDto.CreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public CategoryDto.Response updateCategory(UUID id, CategoryDto.UpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    private CategoryDto.Response mapToResponse(Category category) {
        UUID parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDto.Response(category.getId(), category.getName(), category.getDescription(), parentId);
    }
}
