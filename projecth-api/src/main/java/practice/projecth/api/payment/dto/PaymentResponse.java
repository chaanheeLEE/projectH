package practice.projecth.api.payment.dto;

import practice.projecth.application.payment.dto.PaymentResult;

import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        String idempotencyKey,
        String paymentKey,
        BigDecimal amount,
        String status,
        String failReason
) {
    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.orderId(),
                result.idempotencyKey(),
                result.paymentKey(),
                result.amount().getAmount(),
                result.status().name(),
                result.failReason()
        );
    }
}
