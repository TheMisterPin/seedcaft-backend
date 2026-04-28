package com.michele.mocks.service;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.dto.categories.CategoryWithProductsResponse;
import com.michele.mocks.dto.categories.CreateCategoryRequest;
import com.michele.mocks.dto.categories.UpdateCategoryRequest;
import com.michele.mocks.entity.Category;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.mapper.CategoryMapper;
import com.michele.mocks.mapper.ProductMapper;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.repository.ProductRepository;
import com.michele.mocks.specification.CategorySpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public PageResponse<CategoryResponse> getAll(String q, Long parentId, String parentCode, Pageable pageable) {
        Specification<Category> spec = Specification
                .where(CategorySpecifications.textSearch(q))
                .and(CategorySpecifications.hasParentId(parentId))
                .and(CategorySpecifications.hasParentCode(parentCode));

        return PageResponse.from(categoryRepository.findAll(spec, pageable), CategoryMapper::toResponse);
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        applyRequest(category, request);
        return CategoryMapper.toResponse(categoryRepository.save(category));
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

    @Transactional(readOnly = true)
    public CategoryTreeResponse getCategoryTree(Long id) {
        Category rootCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: id=" + id));

        return CategoryMapper.toTreeResponse(rootCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getFullTree() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getParent() == null)
                .map(CategoryMapper::toTreeResponse)
                .toList();
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
        category.setParent(resolveParent(request.parentId(), request.parentCode()));
    }

    private void applyRequest(Category category, UpdateCategoryRequest request) {
        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        category.setParentCode(request.parentCode());
        category.setParent(resolveParent(request.parentId(), request.parentCode()));
    }

    private Category resolveParent(Long parentId, String parentCode) {
        if (parentId != null) {
            return categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: id=" + parentId));
        }

        if (parentCode != null && !parentCode.isBlank()) {
            return categoryRepository.findByCodeIgnoreCase(parentCode.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: code=" + parentCode));
        }

        return null;
    }
}
