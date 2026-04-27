package com.michele.mocks.repository;

import com.michele.mocks.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = { "products" })
    Optional<Category> findWithProductsById(Long id);

    @EntityGraph(attributePaths = { "children" })
    Optional<Category> findWithChildrenById(Long id);
}