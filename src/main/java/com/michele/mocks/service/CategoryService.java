package com.michele.mocks.service;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.dto.categories.UpdateCategoryRequest;
import com.michele.mocks.entity.Category;
import com.michele.mocks.entity.Product;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.repository.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public PageResponse<CategoryResponse> getAll(Pageable pageable) {
        return PageResponse.from(categoryRepository.findAll(pageable), this::mapCategory);
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        applyRequest(category, request);
        return mapCategory(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: id=" + id);
        }

        if (categoryRepository.existsByParentId(id) || productRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Category cannot be deleted because it has children and/or products");
        }

        categoryRepository.deleteById(id);
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        return mapCategory(category);
    }

    public CategoryWithProductsResponse getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findWithProductsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

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

    @Transactional(readOnly = true)
    public CategoryTreeResponse getCategoryTree(Long id) {
        Category rootCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        String rootCode = rootCategory.getCode();
        if (rootCode == null || rootCode.isBlank()) {
            return new CategoryTreeResponse(
                    rootCategory.getId(),
                    rootCategory.getCode(),
                    rootCategory.getName(),
                    rootCategory.getDescription(),
                    List.of());
        }

        Map<String, List<Category>> categoriesByParentCode = groupByParentCode(categoryRepository.findAll());
        return mapTree(rootCategory, categoriesByParentCode);
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getFullTree() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return List.of();
        }

        Map<String, List<Category>> categoriesByParentCode = groupByParentCode(categories);
        return categories.stream()
                .filter(category -> category.getParentCode() == null || category.getParentCode().isBlank())
                .map(root -> mapTree(root, categoriesByParentCode))
                .toList();
    }

    private Map<String, List<Category>> groupByParentCode(List<Category> categories) {
        return categories.stream()
                .collect(Collectors.groupingBy(
                        category -> normalize(category.getParentCode()),
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)));
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

    private CategoryTreeResponse mapTree(Category category, Map<String, List<Category>> categoriesByParentCode) {
        List<CategoryTreeResponse> children = categoriesByParentCode
                .getOrDefault(normalize(category.getCode()), List.of())
                .stream()
                .map(child -> mapTree(child, categoriesByParentCode))
                .toList();

        return new CategoryTreeResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                children);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
