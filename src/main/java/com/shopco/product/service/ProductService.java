package com.shopco.product.service;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ResponseEntity<ApiResponse>createProduct(@RequestBody ProductRequest productRequest);
}
