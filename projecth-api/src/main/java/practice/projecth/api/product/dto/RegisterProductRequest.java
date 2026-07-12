package practice.projecth.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RegisterProductRequest {
    private String name;
    private BigDecimal price;
    private int initialStock;
    private Long categoryId;

    public RegisterProductRequest(String name, BigDecimal price, int initialStock, Long categoryId) {
        this.name = name;
        this.price = price;
        this.initialStock = initialStock;
        this.categoryId = categoryId;
    }
}
