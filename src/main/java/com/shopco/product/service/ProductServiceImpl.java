package com.shopco.product.service;

import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.core.utils.PaginationUtility;
import com.shopco.core.utils.StringUtil;
import com.shopco.product.enums.Category;
import com.shopco.product.enums.Size;
import com.shopco.product.builder.PublicProductResponseBuilder;
import com.shopco.product.dto.request.ProductRequest;
import com.shopco.product.dto.request.PublicProductListParams;
import com.shopco.product.dto.response.ProductResponse;
import com.shopco.product.dto.response.ProductVariantResponse;
import com.shopco.product.dto.response.PublicProductResponse;
import com.shopco.product.entity.Product;
import com.shopco.product.entity.ProductVariant;
import com.shopco.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("ProductService")
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
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
                                                    v.getId(),
                                                    v.getColor(),
                                                    v.getSize() != null ? v.getSize().name() : null,
                                                    v.getStock()
                                            ))
                                            .toList()
                            )
                            .build();
                })
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0,  "Products fetched successfully", content, "" ));
    }

    /**
     * @param params 
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> handleFetchProductForUsers(PublicProductListParams params) {

        Category category = Category.fromString(params.getCategory());
        Size size = Size.fromString(params.getSize());
        String q   = (params.getSearch() == null || params.getSearch().isBlank()) ? null : "%" + params.getSearch().trim().toLowerCase() + "%";
        Pageable pageable = PaginationUtility.createPageRequest(params.getPage(), params.getPageSize(), mapSort(params.getSort()));
        Set<String> colors = StringUtil.parseLowerCsv(params.getColors());

        Page<Product> paginatedProduct = productRepository.findPublicPage(
                category, q, params.getMinPrice(), params.getMaxPrice(), size, colors, pageable
        );

        List<PublicProductResponse> response = paginatedProduct.getContent().stream().map(PublicProductResponseBuilder::toProduct).toList();

        Map<String, Object> meta  = PaginationUtility.buildPaginationMetadata(paginatedProduct);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "product fetched", response, meta));
    }

    /**
     * @return 
     */
    @Override
    public ResponseEntity<ApiResponse> handleFetchProductVariantsByProductId(UUID productId) {

        Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("product not found"));

        List<ProductVariant> variants = product.getProductVariants();

        List<ProductVariantResponse> response = variants.stream()
                        .map(v -> ProductVariantResponse.builder()
                                .id(v.getId())
                                .color(v.getColor() != null ? v.getColor().toLowerCase() : null)
                                .size(v.getSize() != null ? v.getSize().name() : null)
                                .stock(v.getStock())
                                .build()).toList();

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.success(0, "variants fetched successfully", response, null));
    }

    private Sort mapSort(String s) {
        String k = (s == null ? "new" : s.trim().toLowerCase());
        return switch (k) {
            case "top" -> Sort.by(Sort.Order.desc("createdAt"));
            case "price_asc" -> Sort.by(Sort.Order.asc("price"), Sort.Order.desc("createdAt"));
            case "price_desc" -> Sort.by(Sort.Order.desc("price"), Sort.Order.desc("createdAt"));
//            case "rating" -> Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("createdAt"));
            default -> PaginationUtility.DEFAULT_SORT;// "new"
        };

    }
}
