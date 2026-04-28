package com.michele.mocks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.dto.categories.UpdateCategoryRequest;
import com.michele.mocks.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryResponse create(@RequestBody CreateCategoryRequest request) {
        return service.create(request);
    }

    @PostMapping("/batch")
    public List<CategoryResponse> createBatch(@RequestBody List<CreateCategoryRequest> requests) {
        return service.createAll(requests);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @RequestBody UpdateCategoryRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategory(@PathVariable Long id) {
        return service.getCategory(id);
    }

    @GetMapping("/{id}/with-products")
    public CategoryWithProductsResponse getCategoryWithProducts(@PathVariable Long id) {
        return service.getCategoryWithProducts(id);
    }

    @GetMapping("/{id}/tree")
    public CategoryTreeResponse getCategoryTree(@PathVariable Long id) {
        return service.getCategoryTree(id);
    }
}
