package com.michele.mocks.service;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.dto.categories.UpdateCategoryRequest;
import com.michele.mocks.entity.Category;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.mapper.CategoryMapper;
import com.michele.mocks.mapper.ProductMapper;
import com.michele.mocks.repository.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public PageResponse<CategoryResponse> getAll(Pageable pageable) {
        return PageResponse.from(categoryRepository.findAll(pageable), CategoryMapper::toResponse);
    }

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = new Category();
        applyRequest(category, request);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public List<CategoryResponse> createAll(List<CreateCategoryRequest> requests) {
        List<Category> categories = requests.stream()
                .map(this::toEntity)
                .toList();

        return categoryRepository.saveAll(categories).stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        applyRequest(category, request);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        return CategoryMapper.toResponse(category);
    }

    public CategoryWithProductsResponse getCategoryWithProducts(Long id) {
        Category category = categoryRepository.findWithProductsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        List<CategoryProductResponse> products = category.getProducts()
                .stream()
                .map(ProductMapper::toCategoryProductResponse)
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        return CategoryMapper.toTreeResponse(category);
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
}
