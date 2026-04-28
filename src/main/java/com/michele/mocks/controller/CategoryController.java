package com.michele.mocks.controller;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        return service.create(request);
    }

    @PostMapping("/batch")
    public List<CategoryResponse> createBatch(@Valid @RequestBody List<CreateCategoryRequest> requests) {
        return service.createAll(requests);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
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

    @GetMapping("/tree")
    public List<CategoryTreeResponse> getFullTree() {
        return service.getFullTree();
    }
}
