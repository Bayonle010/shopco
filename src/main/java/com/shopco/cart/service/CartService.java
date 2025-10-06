package com.shopco.cart.service;

import com.shopco.cart.dto.request.CartRequest;
import com.shopco.cart.dto.request.UpdateCartItemRequest;
import com.shopco.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.lang.annotation.Repeatable;
import java.util.UUID;

public interface CartService {
    ResponseEntity<ApiResponse> handleAddItemToCart(CartRequest request, Authentication authentication);
    ResponseEntity<ApiResponse> handleFetchCartForUser(Authentication authentication);
    ResponseEntity<ApiResponse> handleUpdateQuantityForCartItem(UpdateCartItemRequest request, Authentication authentication);
}
