package com.shopco.product.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.product.dto.request.PublicProductListParams;
import com.shopco.product.enums.Category;
import com.shopco.product.enums.Size;
import com.shopco.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/public/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }



    @Operation(
            summary = "Fetch products for public users",
            description = """
        Retrieves a paginated list of products available to public users. 
        This endpoint supports advanced filtering, searching, and sorting.

        **Query Parameters**:

        - **page** *(int, required)*: The page number to retrieve (minimum 1, maximum 200).
        - **pageSize** *(int, required)*: Number of products per page (minimum 1, maximum 35).
        - **category** *(string, optional)*: Filter products by category (e.g., CASUAL, FORMAL, PARTY, GYM or all).
        - **search** *(string, optional)*: Text search across product name and description.
        - **colors** *(string, optional)*: Comma-separated list of colors to filter by (e.g., `red,blue,black`).
        - **size** *(string, optional)*: Filter by product size (e.g., S, M, L, XL).
        - **sort** *(string, optional)*: Sorting mode. Supported values:
            - `new` (default): Most recently added products first.
            - `top`: Sort by top-selling products.
            - `price_asc`: Sort by price (lowest to highest).
            - `price_desc`: Sort by price (highest to lowest).
        - **minPrice** *(decimal, optional)*: Minimum price boundary.
        - **maxPrice** *(decimal, optional)*: Maximum price boundary.

        **Notes**:
        - This endpoint does not return product variants directly. 
        - To fetch variants (color, size, stock) for a specific product, use 
          `GET /api/public/products/{productId}/variants`.
    """
    )

    @GetMapping("")
    public ResponseEntity<ApiResponse> handleFetchPublicProduct(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "35", required = false) int pageSize,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String colors,
            @RequestParam(required = false) Size size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
                                                                ){
        return productService.handleFetchProductForUsers(page, pageSize, category, search, colors, size,
                sort, minPrice, maxPrice
        );
    }


    @Operation(
            summary = "Fetch product variants by product ID",
            description = """
        Retrieves all available variants for a specific product.

        **Path Parameter**:
        - **productId** *(UUID, required)*: The unique identifier of the product.

        **Notes**:
        - Variants provide details that are **not returned** in the main product listing.
        - If no variants exist for the given product ID, an empty list is returned.
        - This endpoint is typically called after fetching products from\s
          `GET /api/public/products` to display the detailed options for a product.
   \s"""
    )
    @GetMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse> handleFetchProductVariantsByProductId(@PathVariable  UUID productId){
        return productService.handleFetchProductVariantsByProductId(productId);
    }
}
