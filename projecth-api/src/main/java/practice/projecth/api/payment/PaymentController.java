package practice.projecth.api.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.projecth.api.payment.dto.PaymentPrepareRequest;
import practice.projecth.api.payment.dto.PaymentResponse;
import practice.projecth.api.payment.dto.PaymentVerifyRequest;
import practice.projecth.application.payment.dto.PaymentPrepareCommand;
import practice.projecth.application.payment.dto.PaymentResult;
import practice.projecth.application.payment.dto.PaymentVerifyCommand;
import practice.projecth.application.payment.usecase.PaymentUseCase;
import practice.projecth.domain.common.Money;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    /**
     * 1. 결제 준비 API (INITIATED 상태 생성 및 멱등키 등록)
     */
    @PostMapping("/prepare")
    public ResponseEntity<PaymentResponse> preparePayment(@Valid @RequestBody PaymentPrepareRequest request) {
        PaymentPrepareCommand command = new PaymentPrepareCommand(
                request.orderId(),
                request.idempotencyKey(),
                Money.wons(request.amount())
        );

        PaymentResult result = paymentUseCase.preparePayment(command);
        return ResponseEntity.ok(PaymentResponse.from(result));
    }

    /**
     * 2. 결제 승인 및 위변조 검증 API (PortOne 단건 조회 및 금액 검증 -> APPROVED / COMPLETED)
     */
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyAndApprovePayment(@Valid @RequestBody PaymentVerifyRequest request) {
        PaymentVerifyCommand command = new PaymentVerifyCommand(
                request.idempotencyKey(),
                request.paymentKey()
        );

        PaymentResult result = paymentUseCase.verifyAndApprovePayment(command);
        return ResponseEntity.ok(PaymentResponse.from(result));
    }

    /**
     * 3. 멱등키 기준 결제 상세 조회 API
     */
    @GetMapping("/{idempotencyKey}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String idempotencyKey) {
        PaymentResult result = paymentUseCase.getPaymentByIdempotencyKey(idempotencyKey);
        return ResponseEntity.ok(PaymentResponse.from(result));
    }
}
