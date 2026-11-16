package com.zayna.marketplace.service;

import com.zayna.exception.ResourceNotFoundException;
import com.zayna.marketplace.dto.ProductRequest;
import com.zayna.marketplace.dto.ProductResponse;
import com.zayna.marketplace.entity.Product;
import com.zayna.marketplace.enumeration.ProductCategory;
import com.zayna.marketplace.enumeration.ProductSort;
import com.zayna.marketplace.repository.ProductRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /** Même logique que le filtre Flutter : catégorie + recherche sur le nom + tri. */
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String category, String query, String sort) {
        final ProductCategory cat = ProductCategory.fromParam(category);
        final ProductSort productSort = ProductSort.fromParam(sort);

        Specification<Product> spec = (root, cq, cb) -> cb.conjunction();
        if (cat != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("category"), cat));
        }
        if (query != null && !query.isBlank()) {
            final String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        return repository.findAll(spec, productSort.toSort()).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return ProductResponse.from(repository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        apply(product, request);
        return ProductResponse.from(repository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    public List<String> categories() {
        return java.util.Arrays.stream(ProductCategory.values()).map(Enum::name).toList();
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non disponible"));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setImageUrl(request.imageUrl());
        product.setSizes(request.sizes() == null ? new ArrayList<>() : new ArrayList<>(request.sizes()));
        product.setStock(request.stock() == null ? 0 : request.stock());
    }
}
