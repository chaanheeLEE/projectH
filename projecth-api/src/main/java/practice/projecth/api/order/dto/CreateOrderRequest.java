package practice.projecth.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "구매자 ID는 필수입니다.")
    private Long buyerId;

    @NotEmpty(message = "최소 하나 이상의 상품을 주문해야 합니다.")
    @Valid
    private List<OrderItemRequest> items;
}
