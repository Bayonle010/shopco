package com.shopco.product.service;

import com.cloudinary.Api;
import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.dto.request.PublicProductListParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductService {
    ResponseEntity<ApiResponse>createProduct(@RequestBody ProductRequest productRequest);
    ResponseEntity<ApiResponse> handleFetchProductsForAdmin(int page, int pageSize);


    ResponseEntity<ApiResponse> handleFetchProductForUsers(PublicProductListParams params);
    ResponseEntity<ApiResponse> handleFetchProductVariantsByProductId(UUID productId);


}
