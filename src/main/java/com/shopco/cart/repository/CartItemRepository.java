package com.shopco.cart.repository;

import com.shopco.cart.entity.CartItem;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCart_IdAndProduct_IdAndProductVariant_Id(UUID cartId, UUID productId, UUID productVariantId);
}
