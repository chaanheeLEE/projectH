package practice.projecth.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "상품 가격은 필수입니다.")
    @PositiveOrZero(message = "상품 가격은 0원 이상이어야 합니다.")
    private BigDecimal price;

    @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
    private int quantity;
}
