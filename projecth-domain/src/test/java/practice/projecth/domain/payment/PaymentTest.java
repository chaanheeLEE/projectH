package practice.projecth.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.projecth.domain.common.Money;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    @DisplayName("결제를 최초 생성하면 INITIATED 상태가 된다.")
    void createPayment() {
        Payment payment = Payment.create(1L, "IDEMPOTENCY-KEY-001", Money.wons(10000));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.INITIATED);
        assertThat(payment.getOrderId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualTo(Money.wons(10000));
    }

    @Test
    @DisplayName("정상적인 결제 상태 전이 흐름 (INITIATED -> PG_REQUESTED -> APPROVED -> COMPLETED)")
    void normalPaymentFlow() {
        Payment payment = Payment.create(1L, "IDEMPOTENCY-KEY-001", Money.wons(10000));

        // INITIATED -> PG_REQUESTED
        Payment pgRequested = payment.requestPg();
        assertThat(pgRequested.getStatus()).isEqualTo(PaymentStatus.PG_REQUESTED);

        // PG_REQUESTED -> APPROVED
        Payment approved = pgRequested.approve("IMP-123456", Money.wons(10000));
        assertThat(approved.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(approved.getPaymentKey()).isEqualTo("IMP-123456");

        // APPROVED -> COMPLETED
        Payment completed = approved.complete();
        assertThat(completed.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 금액 위변조 시 결제 승인이 거부된다.")
    void amountMismatchFail() {
        Payment payment = Payment.create(1L, "IDEMPOTENCY-KEY-001", Money.wons(10000)).requestPg();

        // 10,000원 결제 요청이었으나 PG 승인금액이 1,000원으로 위변조된 경우
        assertThatThrownBy(() -> payment.approve("IMP-123456", Money.wons(1000)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("결제 금액 위변조 감지");
    }

    @Test
    @DisplayName("허용되지 않은 상태 전이 시 예외가 발생한다.")
    void invalidStatusTransition() {
        Payment payment = Payment.create(1L, "IDEMPOTENCY-KEY-001", Money.wons(10000));

        // INITIATED 상태에서 곧바로 COMPLETED로 전이 시도
        assertThatThrownBy(payment::complete)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("결제 상태를 [INITIATED]에서 [COMPLETED](으)로 전이할 수 없습니다.");
    }

    @Test
    @DisplayName("최종 실패(FAILED) 상태 도달 후에는 다른 상태로 전이할 수 없다.")
    void terminalStateNoTransition() {
        Payment payment = Payment.create(1L, "IDEMPOTENCY-KEY-001", Money.wons(10000))
            .fail("PG 승인 거절");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailReason()).isEqualTo("PG 승인 거절");

        assertThatThrownBy(payment::complete)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("전이할 수 없습니다.");
    }
}
