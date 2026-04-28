package com.michele.mocks.repository;

import com.michele.mocks.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @EntityGraph(attributePaths = { "products" })
    Optional<Category> findWithProductsById(Long id);

    boolean existsByParentId(Long parentId);

    Optional<Category> findByCodeIgnoreCase(String code);
}
