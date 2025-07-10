package com.shopco.product.repository;

import com.shopco.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRespository extends JpaRepository<Product, UUID> {
}
