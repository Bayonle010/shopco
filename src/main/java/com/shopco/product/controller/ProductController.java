package com.shopco.product.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.PublicProductListParams;
import com.shopco.product.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/public/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("")
    public ResponseEntity<ApiResponse> handleFetchPublicProduct(@ModelAttribute PublicProductListParams params){
        return productService.handleFetchProductForUsers(params);
    }
}
