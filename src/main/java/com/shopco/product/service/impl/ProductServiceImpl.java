package com.shopco.product.service.impl;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.enums.Category;
import com.shopco.enums.Size;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.entity.Product;
import com.shopco.product.entity.ProductVariant;
import com.shopco.product.repository.ProductRespository;
import com.shopco.product.repository.ProductVariantRepository;
import com.shopco.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("ProductService")
public class ProductServiceImpl implements ProductService {
    private final ProductRespository productRepository;

    public ProductServiceImpl(ProductRespository productRepository) {
        this.productRepository = productRepository;
    }


    @Transactional
    @Override
    public ResponseEntity<ApiResponse> createProduct(ProductRequest productRequest) {

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(Category.fromString(productRequest.getCategory()));
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setCreatedAt(LocalDateTime.now());

        List<ProductVariant> variants = productRequest.getVariants()
                .stream()
                .map(productVariantRequest->{
                    ProductVariant productVariant = new ProductVariant();
                    productVariant.setColor(productVariantRequest.getColor());
                    productVariant.setSize(Size.fromString(productVariantRequest.getSize()));
                    productVariant.setStock(productVariantRequest.getStock());
                    productVariant.setProduct(product);
                    return productVariant;

                }).toList();

        product.setProductVariants(variants);
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success(0, "product uploaded successfully", product.getId(), null));


    }
}
