package com.shopco.product.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "")
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductServiceImpl productService;

    public AdminProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createProduct(@Valid  @RequestBody  ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

}
