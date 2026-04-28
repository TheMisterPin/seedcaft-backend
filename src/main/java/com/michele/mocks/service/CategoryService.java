package com.michele.mocks.service;

import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.entity.Category;
import com.michele.mocks.entity.Product;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.specification.CategorySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryResponse> getAll(
            String q,
            Long parentId,
            String parentCode,
            Pageable pageable) {
        Specification<Category> specification = Specification.where(CategorySpecifications.textSearch(q))
                .and(CategorySpecifications.hasParentId(parentId))
                .and(CategorySpecifications.hasParentCode(parentCode));

        return categoryRepository.findAll(specification, pageable)
                .map(this::mapCategory);
    }

    @Transactional
    public CategoryResponse create(Category category) {
        return mapCategory(categoryRepository.save(category));
    }

    @Transactional
    public List<CategoryResponse> createAll(List<Category> categories) {
        return categoryRepository.saveAll(categories).stream()
                .map(this::mapCategory)
                .toList();
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapCategory(category);
    }

    public CategoryWithProductsResponse getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findWithProductsById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<CategoryProductResponse> products = category.getProducts()
                .stream()
                .map(this::mapCategoryProduct)
                .toList();

        return new CategoryWithProductsResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                products);
    }

    public CategoryTreeResponse getCategoryTree(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapTree(category);
    }

    private CategoryResponse mapCategory(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription());
    }

    private CategoryProductResponse mapCategoryProduct(Product product) {
        return new CategoryProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getSellPrice() != null ? product.getSellPrice().doubleValue() : null,
                product.getPurchPrice() != null ? product.getPurchPrice().doubleValue() : null,
                product.getCurrency());
    }

    private CategoryTreeResponse mapTree(Category category) {
        List<CategoryTreeResponse> children = category.getChildren()
                .stream()
                .map(this::mapTree)
                .toList();

        return new CategoryTreeResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                children);
    }
}
