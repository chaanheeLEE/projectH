package practice.projecth.api.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentVerifyRequest(
        @NotBlank(message = "멱등성 키는 필수입니다.")
        String idempotencyKey,

        @NotBlank(message = "PG 결제 승인 번호(paymentKey)는 필수입니다.")
        String paymentKey
) {
}
