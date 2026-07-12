package practice.projecth.api.cart.dto;

import lombok.Getter;
import practice.projecth.domain.cart.CartItem;

import java.math.BigDecimal;

@Getter
public class CartItemResponse {
    private final Long productId;
    private final int quantity;
    private final BigDecimal priceSnapshot;
    private final BigDecimal totalPrice;

    public CartItemResponse(Long productId, int quantity, BigDecimal priceSnapshot, BigDecimal totalPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceSnapshot = priceSnapshot;
        this.totalPrice = totalPrice;
    }

    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getPriceSnapshot().getAmount(),
                item.getTotalPrice().getAmount()
        );
    }
}
