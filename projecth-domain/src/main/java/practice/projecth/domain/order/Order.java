package practice.projecth.domain.order;

import lombok.Getter;
import practice.projecth.domain.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Order {
    private final Long id;
    private final Long buyerId;
    private final List<OrderItem> orderItems;
    private final Money totalPrice;
    private final OrderStatus status;
    private final Long version;

    public Order(Long id, Long buyerId, List<OrderItem> orderItems, OrderStatus status, Long version) {
        if (buyerId == null) {
            throw new IllegalArgumentException("구매자 ID는 필수입니다.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("최소 하나 이상의 주문 상품이 있어야 합니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("주문 상태는 필수입니다.");
        }
        this.id = id;
        this.buyerId = buyerId;
        this.orderItems = Collections.unmodifiableList(new ArrayList<>(orderItems));
        this.totalPrice = calculateTotalPrice(orderItems);
        this.status = status;
        this.version = version;
    }

    public static Order create(Long buyerId, List<OrderItem> orderItems) {
        return new Order(null, buyerId, orderItems, OrderStatus.PENDING, null);
    }

    private Money calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.ZERO, Money::plus);
    }

    public Order completeStockDeduction() {
        verifyStatusTransition(OrderStatus.STOCK_DEDUCTED);
        return new Order(this.id, this.buyerId, this.orderItems, OrderStatus.STOCK_DEDUCTED, this.version);
    }

    public Order approvePayment() {
        verifyStatusTransition(OrderStatus.PAYMENT_APPROVED);
        return new Order(this.id, this.buyerId, this.orderItems, OrderStatus.PAYMENT_APPROVED, this.version);
    }

    public Order cancel(String reason) {
        verifyStatusTransition(OrderStatus.CANCELLED);
        return new Order(this.id, this.buyerId, this.orderItems, OrderStatus.CANCELLED, this.version);
    }

    public Order fail(String reason) {
        verifyStatusTransition(OrderStatus.FAILED);
        return new Order(this.id, this.buyerId, this.orderItems, OrderStatus.FAILED, this.version);
    }

    private void verifyStatusTransition(OrderStatus nextStatus) {
        if (this.status == OrderStatus.CANCELLED || this.status == OrderStatus.FAILED) {
            throw new IllegalStateException("이미 최종 상태(취소/실패)에 도달한 주문은 상태를 변경할 수 없습니다. 현재 상태: " + this.status);
        }

        if (nextStatus == OrderStatus.STOCK_DEDUCTED && this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("주문 대기(PENDING) 상태에서만 재고 차감 완료 상태로 변경할 수 있습니다.");
        }

        if (nextStatus == OrderStatus.PAYMENT_APPROVED && this.status != OrderStatus.STOCK_DEDUCTED) {
            throw new IllegalStateException("재고 차감 완료(STOCK_DEDUCTED) 상태에서만 결제 승인 완료 상태로 변경할 수 있습니다.");
        }
    }
}
