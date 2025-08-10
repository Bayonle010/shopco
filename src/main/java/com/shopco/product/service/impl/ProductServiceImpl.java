package com.shopco.product.service.impl;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.PageResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.utils.PaginationUtility;
import com.shopco.enums.Category;
import com.shopco.enums.Size;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.dto.response.ProductResponse;
import com.shopco.product.dto.response.ProductVariantResponse;
import com.shopco.product.entity.Product;
import com.shopco.product.entity.ProductVariant;
import com.shopco.product.repository.ProductRespository;
import com.shopco.product.repository.ProductVariantRepository;
import com.shopco.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        product.setImageUrl(productRequest.getImageUrl());
        product.setDiscount(productRequest.getDiscountInPercentage());
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

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
//                .name(product.getName())
//                .category(product.getCategory().toString())
//                .price(product.getPrice())
//                .imageUrl(product.getImageUrl())
//                .description(product.getDescription())
//                .discountInPercentage(product.getDiscount())
//                .variants(product.getProductVariants())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success(0, "product uploaded successfully", response, null));


    }

    /**
     * @param pageSize 
     * @param page
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> handleFetchProductsForAdmin(int page, int pageSize) {

        Pageable pageable = PaginationUtility.createPageRequest(page, pageSize);

        // page ONLY products
        Page<Product> productPage = productRepository.findAll(pageable);

       //fetch variants for these products in one go
        List<UUID> ids = productPage.getContent().stream().map(Product::getId).toList();

        List<Product> hydrated = ids.isEmpty() ? List.of() : productRepository.findByIdIn(ids);

        // Index by id to keep original page order
        Map<UUID, Product> byId = hydrated.stream().collect(Collectors.toMap(Product::getId, p -> p));

        // Step 3: map to DTOs
        List<ProductResponse> content = productPage.getContent().stream()
                .map(p -> {
                    Product full = byId.getOrDefault(p.getId(), p); // fallback just in case
                    return ProductResponse.builder()
                            .id(full.getId())
                            .name(full.getName())
                            .description(full.getDescription())
                            .price(full.getPrice())
                            .discountInPercentage(full.getDiscount())
                            .imageUrl(full.getImageUrl())
                            .category(full.getCategory() != null ? full.getCategory().name() : null)
                            .variants(
                                    (full.getProductVariants() == null ? List.<ProductVariant>of() : full.getProductVariants())
                                            .stream()
                                            .map(v -> new ProductVariantResponse(
                                                    v.getColor(),
                                                    v.getSize() != null ? v.getSize().name() : null,
                                                    v.getStock()
                                            ))
                                            .toList()
                            )
                            .build();
                })
                .toList();






        //List<UUID> ids = productPage.getContent()


//        Page<ProductResponse> productResponsePage = productRepository.findAll(pageable).map(p ->
//                ProductResponse.builder()
//                        .id(p.getId())
//                        .name(p.getName())
//                        .description(p.getDescription())
//                        .price(p.getPrice())
//                        .discountInPercentage(p.getDiscount())
//                        .imageUrl(p.getImageUrl())
//                        .category(p.getCategory() != null ? p.getCategory().name() : null) // enum -> String
//                        .variants(
//                                p.getProductVariants().stream()
//                                        .map(v -> new ProductVariantResponse(
//                                                v.getColor(),
//                                                v.getSize() != null ? v.getSize().name() : null, // enum -> String
//                                                v.getStock()
//                                        ))
//                                        .toList()
//                        )
//                        .build()
//        );
//


        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0,  "Products fetched successfully", content, "" ));

    }
}
