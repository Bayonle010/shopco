package com.shopco.cart.service;

import com.shopco.cart.dto.request.CartRequest;
import com.shopco.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface CartService {
    ResponseEntity<ApiResponse> handleAddItemToCart(CartRequest request, Authentication authentication);
}
