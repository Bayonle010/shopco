package com.shopco.product.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.service.ProductService;
import com.shopco.product.service.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products (Admin) ")
@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Create a new product",
            description = """
        Creates a new product with color variants and stock per size.

        ✅ Required fields:
        - name: Product name
        - description: Product description
        - price: Product price
        - category: Must be one of the predefined enum values:
            → GYM, CASUAL, FORMAL, SPORTS
        - variants: List of color variants (with stock per size)
        - size: → S,M,L,XL,XXL  \s
        ❗ If you send an unknown enum (e.g., invalid category or size), the request will fail with a 400 Bad Request.
       \s"""
    )
    @PostMapping()
    public ResponseEntity<ApiResponse> createProduct(@Valid  @RequestBody  ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> handleFetchProductsForAdmin(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize){
        return productService.handleFetchProductsForAdmin(page, pageSize);
    }



}
