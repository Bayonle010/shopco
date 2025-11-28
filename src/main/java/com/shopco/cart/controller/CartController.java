package com.shopco.cart.controller;

import com.shopco.cart.dto.request.CartRequest;
import com.shopco.cart.dto.request.UpdateCartItemRequest;
import com.shopco.cart.service.CartService;
import com.shopco.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Tag(name = "Carts")
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;


    @Operation(summary = "add to cart")
    @PostMapping("")
    public ResponseEntity<ApiResponse> addItemsToCart(@Valid @RequestBody CartRequest request, Authentication authentication){
        return cartService.handleAddItemToCart(request, authentication);
    }

    @Operation(summary = "Fetch full cart details (item + summary)")
    @GetMapping("")
    public ResponseEntity<ApiResponse> fetchCartForUsers(Authentication authentication){
        return cartService.handleFetchCartForUser(authentication);
    }

    @Operation(summary = "update quantity for a cart item")
    @PostMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse> updateQuantityForCartItem(@PathVariable UUID cartItemId, UpdateCartItemRequest request, Authentication authentication){
        return cartService.handleUpdateQuantityForCartItem(cartItemId, request, authentication);
    }

    @Operation(summary = "remove item from cart")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable UUID cartItemId, Authentication authentication){
        return cartService.handleRemoveCartItem(cartItemId, authentication);
    }


}
