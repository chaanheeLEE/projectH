package practice.projecth.api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCartItemRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private int quantity;

    public UpdateCartItemRequest(Long memberId, int quantity) {
        this.memberId = memberId;
        this.quantity = quantity;
    }
}
