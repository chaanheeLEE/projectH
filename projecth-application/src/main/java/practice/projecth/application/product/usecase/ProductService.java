package practice.projecth.application.product.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.product.port.ProductRepositoryPort;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.product.Product;
import practice.projecth.domain.product.ProductStatus;
import practice.projecth.domain.product.Stock;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Product registerProduct(String name, BigDecimal price, int initialStock, Long categoryId) {
        Product product = new Product(
                null,
                name,
                new Money(price),
                new Stock(initialStock),
                ProductStatus.PREPARING,
                categoryId
        );
        return productRepositoryPort.save(product);
    }

    @Override
    @Transactional
    public Product updateStatus(Long productId, ProductStatus newStatus) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));
        Product updatedProduct = product.changeStatus(newStatus);
        return productRepositoryPort.save(updatedProduct);
    }

    @Override
    @Transactional
    public Product increaseStock(Long productId, int quantity) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));
        Product updatedProduct = product.increaseStock(quantity);
        return productRepositoryPort.save(updatedProduct);
    }

    @Override
    @Transactional
    public Product decreaseStock(Long productId, int quantity) {
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));
        Product updatedProduct = product.decreaseStock(quantity);
        return productRepositoryPort.save(updatedProduct);
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));
    }
}
