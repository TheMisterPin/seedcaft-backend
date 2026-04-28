package com.michele.mocks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.michele.mocks.dto.products.CreateProductRequest;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.dto.products.UpdateProductRequest;
import com.michele.mocks.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest request) {
        return service.create(request);
    }

    @PostMapping("/batch")
    public List<ProductResponse> createBatch(@RequestBody List<CreateProductRequest> requests) {
        return service.createAll(requests);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @GetMapping("/{id}/with-category")
    public ProductWithCategoryResponse getProductWithCategory(@PathVariable Long id) {
        return service.getProductWithCategory(id);
    }
}
