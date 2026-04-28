package com.michele.mocks.service;

import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.dto.categories.UpdateCategoryRequest;
import com.michele.mocks.entity.Category;
import com.michele.mocks.entity.Product;
import com.michele.mocks.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategory)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = new Category();
        applyRequest(category, request);
        return mapCategory(categoryRepository.save(category));
    }

    @Transactional
    public List<CategoryResponse> createAll(List<CreateCategoryRequest> requests) {
        List<Category> categories = requests.stream()
                .map(this::toEntity)
                .toList();

        return categoryRepository.saveAll(categories).stream()
                .map(this::mapCategory)
                .toList();
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        applyRequest(category, request);
        return mapCategory(categoryRepository.save(category));
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

    private Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        applyRequest(category, request);
        return category;
    }

    private void applyRequest(Category category, CreateCategoryRequest request) {
        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        category.setParentCode(request.parentCode());
    }

    private void applyRequest(Category category, UpdateCategoryRequest request) {
        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        category.setParentCode(request.parentCode());
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
