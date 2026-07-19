package practice.projecth.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.projecth.domain.common.Money;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문 생성 시 기본 상태는 PENDING이며, 총 금액이 주문 항목들의 총합과 일치한다")
    void createOrderSuccess() {
        // given
        OrderItem item1 = new OrderItem(1L, new Money(BigDecimal.valueOf(10000)), 2);
        OrderItem item2 = new OrderItem(2L, new Money(BigDecimal.valueOf(15000)), 1);

        // when
        Order order = Order.create(100L, List.of(item1, item2));

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalPrice()).isEqualTo(new Money(BigDecimal.valueOf(35000)));
        assertThat(order.getBuyerId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("주문 대기 상태에서 재고 차감 완료 상태로 정상 변경된다")
    void completeStockDeductionSuccess() {
        // given
        OrderItem item = new OrderItem(1L, new Money(BigDecimal.valueOf(10000)), 1);
        Order order = Order.create(100L, List.of(item));

        // when
        Order updatedOrder = order.completeStockDeduction();

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.STOCK_DEDUCTED);
    }

    @Test
    @DisplayName("재고 차감 완료 상태에서 결제 승인 완료 상태로 정상 변경된다")
    void approvePaymentSuccess() {
        // given
        OrderItem item = new OrderItem(1L, new Money(BigDecimal.valueOf(10000)), 1);
        Order order = Order.create(100L, List.of(item)).completeStockDeduction();

        // when
        Order updatedOrder = order.approvePayment();

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_APPROVED);
    }

    @Test
    @DisplayName("주문 대기 상태에서 결제 승인 완료 상태로 바로 변경하려고 하면 예외가 발생한다")
    void invalidStatusTransitionThrowsException() {
        // given
        OrderItem item = new OrderItem(1L, new Money(BigDecimal.valueOf(10000)), 1);
        Order order = Order.create(100L, List.of(item));

        // when & then
        assertThatThrownBy(order::approvePayment)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고 차감 완료(STOCK_DEDUCTED) 상태에서만");
    }

    @Test
    @DisplayName("이미 실패한 주문은 상태를 취소하거나 변경할 수 없다")
    void cannotChangeStatusOfFailedOrder() {
        // given
        OrderItem item = new OrderItem(1L, new Money(BigDecimal.valueOf(10000)), 1);
        Order order = Order.create(100L, List.of(item)).fail("품절");

        // when & then
        assertThatThrownBy(() -> order.cancel("단순 변심"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 최종 상태(취소/실패)에 도달한 주문");
    }
}
