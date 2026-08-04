package practice.projecth.application.payment.port;

import practice.projecth.domain.common.Money;

public record PgPaymentInfo(
        String paymentKey,
        String idempotencyKey,
        Money amount,
        String status, // "PAID", "FAILED", "CANCELLED"
        String failReason
) {
}
