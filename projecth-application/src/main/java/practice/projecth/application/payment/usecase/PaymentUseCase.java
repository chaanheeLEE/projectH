package practice.projecth.application.payment.usecase;

import practice.projecth.application.payment.dto.PaymentPrepareCommand;
import practice.projecth.application.payment.dto.PaymentResult;
import practice.projecth.application.payment.dto.PaymentVerifyCommand;

public interface PaymentUseCase {
    PaymentResult preparePayment(PaymentPrepareCommand command);
    PaymentResult verifyAndApprovePayment(PaymentVerifyCommand command);
    PaymentResult getPaymentByIdempotencyKey(String idempotencyKey);
}
