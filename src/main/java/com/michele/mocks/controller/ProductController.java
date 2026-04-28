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
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.entity.Product;
import com.michele.mocks.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse create(@RequestBody Product product) {
        return service.create(product);
    }

    @PostMapping("/batch")
    public List<ProductResponse> createBatch(@RequestBody List<Product> products) {
        return service.createAll(products);
    }

    @GetMapping
    public PageResponse<ProductResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return service.getAll(pageable);
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
