package com.michele.mocks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.michele.mocks.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = { "category" })
    Optional<Product> findWithCategoryById(Long id);
}