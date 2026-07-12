package practice.projecth.api.cart.dto;

import lombok.Getter;
import practice.projecth.domain.cart.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartResponse {
    private final Long memberId;
    private final List<CartItemResponse> items;
    private final BigDecimal totalCartPrice;

    public CartResponse(Long memberId, List<CartItemResponse> items, BigDecimal totalCartPrice) {
        this.memberId = memberId;
        this.items = items;
        this.totalCartPrice = totalCartPrice;
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getMemberId(),
                itemResponses,
                cart.getTotalCartPrice().getAmount()
        );
    }
}
