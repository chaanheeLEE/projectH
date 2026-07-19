package practice.projecth.api.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RegisterProductRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotNull(message = "상품 가격은 필수입니다.")
    @PositiveOrZero(message = "상품 가격은 0원 이상이어야 합니다.")
    private BigDecimal price;

    @PositiveOrZero(message = "초기 재고는 0개 이상이어야 합니다.")
    private int initialStock;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    public RegisterProductRequest(String name, BigDecimal price, int initialStock, Long categoryId) {
        this.name = name;
        this.price = price;
        this.initialStock = initialStock;
        this.categoryId = categoryId;
    }
}
