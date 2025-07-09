package com.shopco.product.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.entity.ProductVariant;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @PostMapping("")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody ProductRequest productRequest) {


    }

}
