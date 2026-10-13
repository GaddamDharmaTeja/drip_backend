package com.dripgrow.api.controller;

import com.dripgrow.api.model.Product;
import com.dripgrow.api.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStockOnly,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "featured") String sort
    ) {
        return productService.findAll(category, crop, brand, maxPrice, inStockOnly, q, sort);
    }

    @GetMapping("/{slug}")
    public Product bySlug(@PathVariable String slug) {
        return productService.findBySlug(slug);
    }
}
