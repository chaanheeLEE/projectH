package practice.projecth.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockRequest {
    private int quantity;

    public StockRequest(int quantity) {
        this.quantity = quantity;
    }
}
