package practice.projecth.application.payment.dto;

public record PaymentVerifyCommand(
        String idempotencyKey,
        String paymentKey
) {
}
