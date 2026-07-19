package practice.projecth.api.product.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockRequest {

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private int quantity;

    public StockRequest(int quantity) {
        this.quantity = quantity;
    }
}
