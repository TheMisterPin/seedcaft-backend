package com.michele.mocks.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.entity.Category;
import com.michele.mocks.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryResponse create(@RequestBody Category category) {
        return service.create(category);
    }

    @PostMapping("/batch")
    public List<CategoryResponse> createBatch(@RequestBody List<Category> categories) {
        return service.createAll(categories);
    }

    @GetMapping
    public PageResponse<CategoryResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.getAll(pageable);
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
