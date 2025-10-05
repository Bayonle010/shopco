package com.shopco.cart.service;

import com.shopco.cart.builder.CartResponseBuilder;
import com.shopco.cart.dto.request.CartRequest;
import com.shopco.cart.dto.response.CartResponse;
import com.shopco.cart.entity.Cart;
import com.shopco.cart.entity.CartItem;
import com.shopco.cart.repository.CartItemRepository;
import com.shopco.cart.repository.CartRepository;
import com.shopco.core.exception.BadCredentialsException;
import com.shopco.core.exception.IllegalArgumentException;
import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.product.entity.Product;
import com.shopco.product.entity.ProductVariant;
import com.shopco.product.repository.ProductRepository;
import com.shopco.product.repository.ProductVariantRepository;
import com.shopco.user.entity.User;
import com.shopco.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service("CartService")
public class CartServiceImpl implements CartService{

    private final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    /**
     * @param request 
     * @return
     */
    @Transactional
    @Override
    public ResponseEntity<ApiResponse> handleAddItemToCart(CartRequest request, Authentication authentication) {
        String authenticatedUserEmail = authentication.getName();
        User user = userRepository.findByEmail(authenticatedUserEmail).orElseThrow(()-> new BadCredentialsException("invalid user"));

        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                ()-> new ResourceNotFoundException("product not found"));

        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId()).orElseThrow(
                ()-> new ResourceNotFoundException("product variant not found"));

        logger.info("product id is ---->> {} ...... while product variant id is --->> {}", productVariant.getProduct().getId(), productVariant.getId());

        if(!Objects.equals(productVariant.getProduct().getId(), product.getId())){
            throw new IllegalArgumentException("product variant does not belong to product");
        }

        if ((productVariant.getStock() < request.getQuantity())){
            throw new IllegalArgumentException("Insufficient stock for the selected variant");
        }

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseGet(()-> {
            Cart c = Cart.builder().user(user).build();
             return cartRepository.save(c);
        });


        BigDecimal unitPriceSnapshot = product.getPrice();
        BigDecimal percentDiscount = product.getPrice();

        Optional<CartItem> existing = cartItemRepository.findByCart_IdAndProduct_IdAndProductVariant_Id(cart.getId(), product.getId(), productVariant.getId());
        CartItem line;
        if (existing.isPresent()){
            line = existing.get();
            line.setQuantity(line.getQuantity() + request.getQuantity());
            line.setUnitPriceSnapshot(unitPriceSnapshot);
        }else {
            line = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .productVariant(productVariant)
                    .unitPriceSnapshot(unitPriceSnapshot)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(line);
        }
        cartItemRepository.save(line);


        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "cart added sucessfully", "", null));
    }

    /**
     * @param authentication 
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> handleFetchCartForUser(Authentication authentication) {
        String authenticatedUserEmail = authentication.getName();
        User user = userRepository.findByEmail(authenticatedUserEmail).orElseThrow(()-> new BadCredentialsException("invalid user"));

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(()-> new ResourceNotFoundException("no cart found for user"));

        CartResponse response = CartResponseBuilder.buildCartResponse(cart);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "cart details successfully fetched", response, null));
    }
}
