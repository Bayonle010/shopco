package com.shopco.product.repository;

import com.shopco.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRespository extends JpaRepository<Product, UUID> {
    @Override
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "productVariants")
    List<Product> findByIdIn(List<UUID> ids);
}
