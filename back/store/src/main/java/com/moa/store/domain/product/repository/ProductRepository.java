package com.moa.store.domain.product.repository;

import com.moa.store.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUuid(UUID productUuid);
    void deleteByUuid(UUID productUuid);
    boolean existsByUuid(UUID productUuid);
}
