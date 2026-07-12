package practice.projecth.application.product.port;

import practice.projecth.domain.product.Product;

import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
}
