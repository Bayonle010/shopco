package com.shopco.cart.controller;

import com.shopco.cart.dto.request.CartRequest;
import com.shopco.cart.service.CartService;
import com.shopco.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "Carts")
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> addItemsToCart(@Valid @RequestBody CartRequest request, Authentication authentication){
        return cartService.handleAddItemToCart(request, authentication);
    }

}
