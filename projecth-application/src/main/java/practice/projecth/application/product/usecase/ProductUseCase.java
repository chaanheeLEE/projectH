package practice.projecth.application.product.usecase;

import practice.projecth.domain.product.Product;
import practice.projecth.domain.product.ProductStatus;

import java.math.BigDecimal;

public interface ProductUseCase {
    Product registerProduct(String name, BigDecimal price, int initialStock, Long categoryId);
    Product updateStatus(Long productId, ProductStatus newStatus);
    Product increaseStock(Long productId, int quantity);
    Product decreaseStock(Long productId, int quantity);
    Product getProduct(Long productId);
}
