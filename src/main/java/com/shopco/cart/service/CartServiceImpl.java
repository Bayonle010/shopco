package com.shopco.cart.service;

import com.shopco.cart.builder.CartResponseBuilder;
import com.shopco.cart.dto.request.CartRequest;
import com.shopco.cart.dto.request.UpdateCartItemRequest;
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
import java.math.RoundingMode;
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

        Optional<CartItem> existing = cartItemRepository.findByCart_IdAndProduct_IdAndProductVariant_Id(cart.getId(), product.getId(), productVariant.getId());
        CartItem line;
        if (existing.isPresent()){
            line = existing.get();
            line.setQuantity(line.getQuantity() + request.getQuantity());
            //line.setUnitPriceSnapshot(unitPriceSnapshot);
        }else {
            line = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .productVariant(productVariant)
                   // .unitPriceSnapshot(unitPriceSnapshot)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(line);
        }
        cartItemRepository.save(line);


        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "cart added successfully", "", null));
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

    /**
     * @param request 
     * @param authentication
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> handleUpdateQuantityForCartItem(UpdateCartItemRequest request, Authentication authentication) {
        if (request.getQuantity() <= 0){
            throw new IllegalArgumentException("quantity be greater than zero");
        }

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new BadCredentialsException("invalid user"));

        Cart cart = cartRepository.findByUser_Id(user.getId()).orElseThrow(() -> new ResourceNotFoundException("cart does not exist for user"));

        CartItem cartItem = cartItemRepository.findById(request.getCartItemId()).orElseThrow(()-> new ResourceNotFoundException("cart item not found"));

        if (!Objects.equals(user.getId(), cart.getUser().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseUtil.error(99, "Access denied to update cart", "cart item does not belong user", ""));
        }

        if(!Objects.equals(cartItem.getCart().getId(), cartItem.getId())){
            throw new IllegalArgumentException("cart item does not belong to user's cart");
        }

        // validate stock rules
        ProductVariant productVariant = cartItem.getProductVariant();
        if (productVariant.getStock() < request.getQuantity()){
            throw new IllegalArgumentException("Insufficient sock for selected variants");
        }

        //update quantity
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        Product product = cartItem.getProduct();
        BigDecimal list = product.getPrice();
        BigDecimal percentageDiscount = BigDecimal.valueOf(product.getDiscount());
        BigDecimal unit = list.subtract(list.multiply(percentageDiscount).divide(BigDecimal.valueOf(100),4, RoundingMode.HALF_UP));

        product.set







        return null;
    }


    private BigDecimal effectiveFrom(BigDecimal listingPrice, double discountPercent){
        if (listingPrice == null) return BigDecimal.ZERO;
        BigDecimal pct = BigDecimal.valueOf(discountPercent);
        if (pct.compareTo(BigDecimal.ZERO) <= 0) return listingPrice;

        return listingPrice.subtract(listingPrice.multiply(pct).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    }

    private void snapShotFromProduct(CartItem cartItem){
        var product = cartItem.getProduct();
        var p = cartItem.getProduct();

        BigDecimal list = p.getPrice() ;
        BigDecimal unit = effectiveFrom(list, p.getDiscount());
        BigDecimal pct  = BigDecimal.valueOf(p.getDiscount());

        cartItem.setListPriceSnapshot(list);
        cartItem.setUnitPriceSnapshot(unit);
        cartItem.setDiscountPercentSnapshot(pct);
        if (cartItem.getCurrency() == null) cartItem.setCurrency("NGN");
    }

    private Cart resolveOrCreateCart(User user){
        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

}
