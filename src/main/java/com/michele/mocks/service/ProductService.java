package com.michele.mocks.service;

import com.michele.mocks.dto.PageResponse;
import com.michele.mocks.dto.products.CreateProductRequest;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.dto.products.UpdateProductRequest;
import com.michele.mocks.entity.Product;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.mapper.ProductMapper;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.repository.ProductRepository;
import com.michele.mocks.specification.ProductSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public List<ProductResponse> createAll(List<CreateProductRequest> requests) {
        List<Product> products = requests.stream()
                .map(this::toEntity)
                .toList();

        return productRepository.saveAll(products).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        applyRequest(product, request);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: id=" + id);
        }
        productRepository.deleteById(id);
    }

    public PageResponse<ProductResponse> getAll(
            String q,
            Long categoryId,
            String categoryCode,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String currency,
            Pageable pageable) {

        Specification<Product> spec = Specification
                .where(ProductSpecifications.textSearch(q))
                .and(ProductSpecifications.hasCategoryId(categoryId))
                .and(ProductSpecifications.hasCategoryCode(categoryCode))
                .and(ProductSpecifications.minPrice(minPrice))
                .and(ProductSpecifications.maxPrice(maxPrice))
                .and(ProductSpecifications.hasCurrency(currency));

        return PageResponse.from(productRepository.findAll(spec, pageable), ProductMapper::toResponse);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        return ProductMapper.toResponse(product);
    }

    public ProductWithCategoryResponse getProductWithCategory(Long id) {
        Product product = productRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));

        return ProductMapper.toWithCategoryResponse(product);
    }

    private Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return product;
    }

    private void applyRequest(Product product, CreateProductRequest request) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBarcode(request.barcode());
        product.setTrackingMode(request.trackingMode());
        product.setUom(request.uom());
        product.setWeightKg(request.weightKg() != null ? request.weightKg() : 0d);
        product.setLengthCm(request.lengthCm() != null ? request.lengthCm() : 0d);
        product.setWidthCm(request.widthCm() != null ? request.widthCm() : 0d);
        product.setHeightCm(request.heightCm() != null ? request.heightCm() : 0d);
        product.setMinQuantity(request.minQuantity() != null ? request.minQuantity() : 0);
        product.setImageUrl(request.imageUrl());
        product.setImageUrls(request.imageUrls());
        product.setSellPrice(request.sellPrice());
        product.setPurchPrice(request.purchasePrice());
        product.setCurrency(request.currency());

        assignCategory(product, request.categoryId());
    }

    private void applyRequest(Product product, UpdateProductRequest request) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBarcode(request.barcode());
        product.setTrackingMode(request.trackingMode());
        product.setUom(request.uom());
        product.setWeightKg(request.weightKg() != null ? request.weightKg() : 0d);
        product.setLengthCm(request.lengthCm() != null ? request.lengthCm() : 0d);
        product.setWidthCm(request.widthCm() != null ? request.widthCm() : 0d);
        product.setHeightCm(request.heightCm() != null ? request.heightCm() : 0d);
        product.setMinQuantity(request.minQuantity() != null ? request.minQuantity() : 0);
        product.setImageUrl(request.imageUrl());
        product.setImageUrls(request.imageUrls());
        product.setSellPrice(request.sellPrice());
        product.setPurchPrice(request.purchasePrice());
        product.setCurrency(request.currency());

        assignCategory(product, request.categoryId());
    }

    private void assignCategory(Product product, Long categoryId) {
        if (categoryId == null) {
            product.setCategory(null);
            return;
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found: id=" + categoryId);
        }

        product.setCategory(categoryRepository.getReferenceById(categoryId));
    }
}
