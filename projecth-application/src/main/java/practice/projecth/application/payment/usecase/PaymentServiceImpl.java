package practice.projecth.application.payment.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.payment.dto.PaymentPrepareCommand;
import practice.projecth.application.payment.dto.PaymentResult;
import practice.projecth.application.payment.dto.PaymentVerifyCommand;
import practice.projecth.application.payment.port.PgClientPort;
import practice.projecth.application.payment.port.PgPaymentInfo;
import practice.projecth.domain.payment.Payment;
import practice.projecth.domain.payment.PaymentRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PgClientPort pgClientPort;

    @Override
    public PaymentResult preparePayment(PaymentPrepareCommand command) {
        log.info("[PaymentPrepare] 결제 준비 - 주문 ID: {}, 멱등키: {}", command.orderId(), command.idempotencyKey());

        // 1. 멱등성 검증: 동일 idempotencyKey로 이미 준비/진행된 결제가 존재하는지 확인
        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(command.idempotencyKey());
        if (existingPayment.isPresent()) {
            log.info("[PaymentPrepare] 이미 존재하는 멱등키 요청입니다. 기존 결제 정보를 반환합니다. IdempotencyKey: {}", command.idempotencyKey());
            return PaymentResult.from(existingPayment.get());
        }

        // 2. 신규 INITIATED 상태 결제 객체 생성 및 저장
        Payment newPayment = Payment.create(command.orderId(), command.idempotencyKey(), command.amount());
        Payment savedPayment = paymentRepository.save(newPayment);

        return PaymentResult.from(savedPayment);
    }

    @Override
    public PaymentResult verifyAndApprovePayment(PaymentVerifyCommand command) {
        log.info("[PaymentVerify] 결제 검증 및 승인 요청 - 멱등키: {}, paymentKey: {}", command.idempotencyKey(), command.paymentKey());

        // 1. DB에서 결제 내역 조회
        Payment payment = paymentRepository.findByIdempotencyKey(command.idempotencyKey())
                .orElseThrow(() -> new IllegalArgumentException("해당 멱등키에 해당하는 결제 정보가 존재하지 않습니다: " + command.idempotencyKey()));

        // 2. 이미 최종 완료(COMPLETED)된 요청인 경우 멱등하게 성공 결과 반환
        if (payment.getStatus() == practice.projecth.domain.payment.PaymentStatus.COMPLETED) {
            log.info("[PaymentVerify] 이미 처리 완료된 결제 건입니다. 멱등 응답 반환. paymentKey: {}", command.paymentKey());
            return PaymentResult.from(payment);
        }

        // 3. PG 승인 요청 진행 상태로 전이 (PG_REQUESTED)
        Payment pgRequestedPayment = payment.requestPg();
        paymentRepository.save(pgRequestedPayment);

        try {
            // 4. PG사 서버 단건 조회 (PG 실제 결제 금액 및 상태 확인)
            PgPaymentInfo pgInfo = pgClientPort.getPaymentInfo(command.paymentKey());

            // 5. 서버 DB 금액 vs PG 실제 승인 금액 검증 및 도메인 승인(APPROVED) 처리
            Payment approvedPayment = pgRequestedPayment.approve(command.paymentKey(), pgInfo.amount());

            // 6. 주문/재고 등 후속 처리 완료 후 COMPLETED 전이
            Payment completedPayment = approvedPayment.complete();
            Payment saved = paymentRepository.save(completedPayment);

            log.info("[PaymentVerify] 결제 금액 검증 및 최종 승인 완료! paymentKey: {}, 금액: {}", command.paymentKey(), pgInfo.amount());
            return PaymentResult.from(saved);

        } catch (Exception e) {
            log.error("[PaymentVerify Error] 결제 검증 실패! 자동 결제 취소(Rollback)를 시도합니다. 사유: {}", e.getMessage());

            // 위변조 또는 검증 실패 시 PG사 취소 요청
            try {
                pgClientPort.cancelPayment(command.paymentKey(), "결제 검증 실패: " + e.getMessage());
            } catch (Exception cancelEx) {
                log.error("[PaymentVerify Cancel Fail] PG 취소 요청 실패: {}", cancelEx.getMessage());
            }

            // DB 결제 상태 FAILED 로 전이 및 실패 사유 기록
            Payment failedPayment = pgRequestedPayment.fail(e.getMessage());
            paymentRepository.save(failedPayment);

            throw new IllegalStateException("결제 검증 실패: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResult getPaymentByIdempotencyKey(String idempotencyKey) {
        return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .map(PaymentResult::from)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다: " + idempotencyKey));
    }
}
