package com.dripgrow.api.service;

import com.dripgrow.api.model.Product;
import com.dripgrow.api.repository.ProductRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll(
            String category,
            String crop,
            String brand,
            Double maxPrice,
            Boolean inStockOnly,
            String q,
            String sort
    ) {
        return productRepository.findAll().stream()
                .filter(p -> category == null || category.isBlank() || category.equalsIgnoreCase(p.getCategory()))
                .filter(p -> brand == null || brand.isBlank() || brand.equalsIgnoreCase(p.getBrand()))
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .filter(p -> inStockOnly == null || !inStockOnly || p.isInStock())
                .filter(p -> crop == null || crop.isBlank()
                        || (p.getCropTypes() != null && p.getCropTypes().stream()
                        .anyMatch(c -> c.equalsIgnoreCase(crop))))
                .filter(p -> q == null || q.isBlank() || matchesQuery(p, q))
                .sorted(sortComparator(sort))
                .toList();
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private boolean matchesQuery(Product p, String q) {
        String needle = q.toLowerCase(Locale.ROOT);
        return (p.getName() != null && p.getName().toLowerCase(Locale.ROOT).contains(needle))
                || (p.getShortDescription() != null
                && p.getShortDescription().toLowerCase(Locale.ROOT).contains(needle));
    }

    private Comparator<Product> sortComparator(String sort) {
        if ("price-asc".equals(sort)) {
            return Comparator.comparingDouble(Product::getPrice);
        }
        if ("price-desc".equals(sort)) {
            return Comparator.comparingDouble(Product::getPrice).reversed();
        }
        if ("rating".equals(sort)) {
            return Comparator.comparingDouble(Product::getRating).reversed();
        }
        return Comparator.comparing(Product::isBestseller).reversed()
                .thenComparing(Product::getName);
    }
}
