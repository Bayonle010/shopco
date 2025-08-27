package com.shopco.product.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/products")
public class ProductController {

}
