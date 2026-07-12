package practice.projecth.domain.product;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Stock {
    private final int quantity;

    public Stock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0보다 작을 수 없습니다.");
        }
        this.quantity = quantity;
    }

    public Stock decrease(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("차감할 재고 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다. (현재 재고: " + this.quantity + ", 요청: " + quantity + ")");
        }
        return new Stock(this.quantity - quantity);
    }

    public Stock increase(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("추가할 재고 수량은 0보다 커야 합니다.");
        }
        return new Stock(this.quantity + quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return quantity == stock.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity);
    }

    @Override
    public String toString() {
        return String.valueOf(quantity);
    }
}
