package practice.projecth.api.product.dto;

import lombok.Getter;
import practice.projecth.domain.product.Product;

import java.math.BigDecimal;

@Getter
public class ProductResponse {
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final int stock;
    private final String status;
    private final Long categoryId;

    public ProductResponse(Long id, String name, BigDecimal price, int stock, String status, Long categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.categoryId = categoryId;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSalePrice().getAmount(),
                product.getStock().getQuantity(),
                product.getStatus().name(),
                product.getCategoryId()
        );
    }
}
