package com.shopco.product.service.impl;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("ProductService")
public class ProductServiceImpl implements ProductService {
    @Override
    public ResponseEntity<ApiResponse> createProduct(ProductRequest productRequest) {
        return null;
    }
}
