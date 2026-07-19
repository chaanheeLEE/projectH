package practice.projecth.domain.order;

import lombok.Getter;
import practice.projecth.domain.common.Money;

import java.util.Objects;

@Getter
public class OrderItem {
    private final Long productId;
    private final Money price;
    private final int quantity;

    public OrderItem(Long productId, Money price, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (price == null || price.getAmount().signum() < 0) {
            throw new IllegalArgumentException("올바른 상품 가격이어야 합니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public Money getTotalPrice() {
        return price.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity &&
                Objects.equals(productId, orderItem.productId) &&
                Objects.equals(price, orderItem.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, price, quantity);
    }
}
