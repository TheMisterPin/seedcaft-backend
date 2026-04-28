package com.michele.mocks.service;

import com.michele.mocks.dto.products.ProductCategoryResponse;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.entity.Category;
import com.michele.mocks.entity.Product;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponse create(Product product) {
        resolveCategory(product);
        return toProductResponse(productRepository.save(product));
    }

    @Transactional
    public List<ProductResponse> createAll(List<Product> products) {
        for (Product product : products) {
            resolveCategory(product);
        }
        return productRepository.saveAll(products).stream()
                .map(ProductService::toProductResponse)
                .toList();
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(ProductService::toProductResponse)
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        return toProductResponse(product);
    }

    private static String categoryIdForResponse(Product product) {
        if (product.getCategory() != null) {
            return String.valueOf(product.getCategory().getId());
        }
        return product.getCategoryId();
    }

    private void resolveCategory(Product product) {
        if (product.getCategory() != null) {
            return;
        }
        String idStr = product.getCategoryId();
        if (idStr == null || idStr.isBlank()) {
            return;
        }
        long categoryPk;
        try {
            categoryPk = Long.parseLong(idStr.trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid categoryId: " + idStr);
        }

        Category category = categoryRepository.findById(categoryPk)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + categoryPk));

        product.setCategory(category);
    }

    private static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                categoryIdForResponse(product),
                product.getImageUrl(),
                product.getSellPrice() != null ? product.getSellPrice().doubleValue() : null,
                product.getPurchPrice() != null ? product.getPurchPrice().doubleValue() : null,
                product.getCurrency());
    }

    public ProductWithCategoryResponse getProductWithCategory(Long id) {
        Product product = productRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        ProductCategoryResponse categoryDto = null;

        if (product.getCategory() != null) {
            var c = product.getCategory();
            categoryDto = new ProductCategoryResponse(
                    c.getId(),
                    c.getName(),
                    c.getDescription(),
                    c.getCode(),
                    c.getParentCode());
        }

        return new ProductWithCategoryResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                categoryDto);
    }
}