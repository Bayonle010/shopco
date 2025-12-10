package com.shopco.product.service;

import com.cloudinary.Api;
import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.dto.request.PublicProductListParams;
import com.shopco.product.enums.Category;
import com.shopco.product.enums.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    ResponseEntity<ApiResponse>createProduct(@RequestBody ProductRequest productRequest, Authentication authentication);
    ResponseEntity<ApiResponse> handleFetchProductsForAdmin(int page, int pageSize, Authentication authentication);

    ResponseEntity<ApiResponse> handleFetchProductForUsers(int page,
                                                           int pageSize,
                                                           Category category,
                                                           String search, String colors, Size size,String sort,
                                                           BigDecimal minPrice, BigDecimal maxPrice);

    ResponseEntity<ApiResponse> handleFetchProductVariantsByProductId(UUID productId);


}
