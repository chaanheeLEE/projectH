package practice.projecth.domain.cart;

import lombok.Getter;
import practice.projecth.domain.common.Money;

import java.util.Objects;

@Getter
public class CartItem {
    private final Long productId;
    private final int quantity;
    private final Money priceSnapshot;

    public CartItem(Long productId, int quantity, Money priceSnapshot) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("장바구니 수량은 0보다 커야 합니다.");
        }
        if (priceSnapshot == null) {
            throw new IllegalArgumentException("가격 스냅샷은 필수입니다.");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.priceSnapshot = priceSnapshot;
    }

    public CartItem changeQuantity(int newQuantity) {
        return new CartItem(this.productId, newQuantity, this.priceSnapshot);
    }

    public Money getTotalPrice() {
        return this.priceSnapshot.multiply(this.quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity &&
                Objects.equals(productId, cartItem.productId) &&
                Objects.equals(priceSnapshot, cartItem.priceSnapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, priceSnapshot);
    }
}
