package com.shopco.product.repository;

import com.shopco.product.enums.Category;
import com.shopco.product.enums.Size;
import com.shopco.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Override
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "productVariants")
    List<Product> findByIdIn(List<UUID> ids);


    @Query(value = """
    SELECT p
    FROM Product p
    WHERE (:category IS NULL OR p.category = :category)
      AND (:search IS NULL OR LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search)
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:size IS NULL OR EXISTS (
            SELECT 1 FROM ProductVariant v
            WHERE v.product = p AND v.size = :size
          ))
      AND (COALESCE(:colors, NULL) IS NULL OR EXISTS (
            SELECT 1 FROM ProductVariant v
            WHERE v.product = p AND LOWER(v.color) IN :colors
          ))
    """,
            countQuery = """
    SELECT COUNT(p)
    FROM Product p
    WHERE (:category IS NULL OR p.category = :category)
      AND (:search IS NULL OR LOWER(p.name) LIKE :search OR LOWER(p.description) LIKE :search)
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:size IS NULL OR EXISTS (
            SELECT 1 FROM ProductVariant v
            WHERE v.product = p AND v.size = :size
          ))
      AND (COALESCE(:colors, NULL) IS NULL OR EXISTS (
            SELECT 1 FROM ProductVariant v
            WHERE v.product = p AND LOWER(v.color) IN :colors
          ))
    """
    )
    Page<Product> findPublicPage(
            @Param("category") Category category,
            @Param("search") String searchLike,          // <-- align name
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("size") Size size,
            @Param("colors") Set<String> colorsLowercased,
            Pageable pageable
    );
}
