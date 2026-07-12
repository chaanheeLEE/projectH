package practice.projecth.api.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCartItemRequest {
    private Long memberId;
    private int quantity;

    public UpdateCartItemRequest(Long memberId, int quantity) {
        this.memberId = memberId;
        this.quantity = quantity;
    }
}
