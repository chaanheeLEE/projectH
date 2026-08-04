package practice.projecth.application.payment.dto;

import practice.projecth.domain.common.Money;
import practice.projecth.domain.payment.Payment;
import practice.projecth.domain.payment.PaymentStatus;

public record PaymentResult(
        Long paymentId,
        Long orderId,
        String idempotencyKey,
        String paymentKey,
        Money amount,
        PaymentStatus status,
        String failReason
) {
    public static PaymentResult from(Payment payment) {
        return new PaymentResult(
                payment.getId(),
                payment.getOrderId(),
                payment.getIdempotencyKey(),
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getFailReason()
        );
    }
}
