package com.shopco.cart.service;

import com.shopco.cart.dto.CartRequest;
import com.shopco.core.response.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

public interface CartService {
    ResponseEntity<ApiResponse> handleAddItemToCart(CartRequest request, Authentication authentication);
}
