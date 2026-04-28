package com.michele.mocks.controller;

import java.math.BigDecimal;
import java.util.List;

import com.michele.mocks.dto.products.UpdateProductRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.michele.mocks.dto.products.CreateProductRequest;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.service.ProductService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return service.create(request);
    }

    @PostMapping("/batch")
    public List<ProductResponse> createBatch(@Valid @RequestBody List<CreateProductRequest> requests) {
        return service.createAll(requests);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String currency,
            Pageable pageable) {
        return service.getAll(q, categoryId, categoryCode, minPrice, maxPrice, currency, pageable);
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
