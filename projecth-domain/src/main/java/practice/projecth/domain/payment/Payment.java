package practice.projecth.domain.payment;

import lombok.Getter;
import practice.projecth.domain.common.Money;

import java.util.Objects;

@Getter
public class Payment {
    private final Long id;
    private final Long orderId;
    private final String idempotencyKey;
    private final String paymentKey; // PG 거래 고유번호 (imp_uid / paymentId)
    private final Money amount;
    private final PaymentStatus status;
    private final String failReason;
    private final Long version;

    public Payment(Long id, Long orderId, String idempotencyKey, String paymentKey, Money amount, PaymentStatus status, String failReason, Long version) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("멱등성 키는 필수입니다.");
        }
        if (amount == null || amount.isLessThanOrEqualZero()) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("결제 상태는 필수입니다.");
        }

        this.id = id;
        this.orderId = orderId;
        this.idempotencyKey = idempotencyKey;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.failReason = failReason;
        this.version = version;
    }

    public static Payment create(Long orderId, String idempotencyKey, Money amount) {
        return new Payment(null, orderId, idempotencyKey, null, amount, PaymentStatus.INITIATED, null, null);
    }

    public Payment requestPg() {
        this.status.verifyTransitionTo(PaymentStatus.PG_REQUESTED);
        return new Payment(this.id, this.orderId, this.idempotencyKey, this.paymentKey, this.amount, PaymentStatus.PG_REQUESTED, null, this.version);
    }

    public Payment approve(String paymentKey, Money paidAmount) {
        this.status.verifyTransitionTo(PaymentStatus.APPROVED);

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("PG 승인 거래 키(paymentKey)는 필수입니다.");
        }

        // 서버 주문 금액 vs PG 실제 결제 금액 위변조 검증
        if (!Objects.equals(this.amount, paidAmount)) {
            throw new IllegalStateException(
                String.format("결제 금액 위변조 감지! 요청 금액: %s, PG 실제 승인 금액: %s", this.amount, paidAmount)
            );
        }

        return new Payment(this.id, this.orderId, this.idempotencyKey, paymentKey, this.amount, PaymentStatus.APPROVED, null, this.version);
    }

    public Payment complete() {
        this.status.verifyTransitionTo(PaymentStatus.COMPLETED);
        return new Payment(this.id, this.orderId, this.idempotencyKey, this.paymentKey, this.amount, PaymentStatus.COMPLETED, null, this.version);
    }

    public Payment fail(String reason) {
        this.status.verifyTransitionTo(PaymentStatus.FAILED);
        return new Payment(this.id, this.orderId, this.idempotencyKey, this.paymentKey, this.amount, PaymentStatus.FAILED, reason, this.version);
    }

    public Payment cancel(String reason) {
        this.status.verifyTransitionTo(PaymentStatus.CANCELLED);
        return new Payment(this.id, this.orderId, this.idempotencyKey, this.paymentKey, this.amount, PaymentStatus.CANCELLED, reason, this.version);
    }
}
