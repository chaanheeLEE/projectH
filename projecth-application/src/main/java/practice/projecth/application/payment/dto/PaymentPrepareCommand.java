package practice.projecth.application.payment.dto;

import practice.projecth.domain.common.Money;

public record PaymentPrepareCommand(
        Long orderId,
        String idempotencyKey,
        Money amount
) {
}
