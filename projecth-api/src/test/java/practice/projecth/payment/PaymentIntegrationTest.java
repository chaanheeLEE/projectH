package practice.projecth.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.payment.dto.PaymentPrepareCommand;
import practice.projecth.application.payment.dto.PaymentResult;
import practice.projecth.application.payment.dto.PaymentVerifyCommand;
import practice.projecth.application.payment.usecase.PaymentUseCase;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.payment.PaymentStatus;
import practice.projecth.infrastructure.pg.PortOnePgClientAdapter;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PaymentIntegrationTest {

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private PortOnePgClientAdapter portOnePgClientAdapter;

    @Test
    @DisplayName("결제 준비 요청 시 INITIATED 상태로 저장되며, 동일 멱등키 재요청 시 기존 객체를 반환한다 (멱등성 검증)")
    void preparePaymentIdempotency() {
        // given
        String idempotencyKey = UUID.randomUUID().toString();
        PaymentPrepareCommand command = new PaymentPrepareCommand(1L, idempotencyKey, Money.wons(10000));

        // when
        PaymentResult firstResult = paymentUseCase.preparePayment(command);
        PaymentResult secondResult = paymentUseCase.preparePayment(command);

        // then
        assertThat(firstResult.paymentId()).isEqualTo(secondResult.paymentId());
        assertThat(firstResult.idempotencyKey()).isEqualTo(idempotencyKey);
        assertThat(firstResult.status()).isEqualTo(PaymentStatus.INITIATED);
    }

    @Test
    @DisplayName("PG 결제 승인 요청 시 실제 결제 금액이 주문 금액과 일치하면 COMPLETED 상태로 전환된다")
    void verifyAndApprovePaymentSuccess() {
        // given
        String idempotencyKey = UUID.randomUUID().toString();
        String paymentKey = "imp_test_" + UUID.randomUUID();
        Money amount = Money.wons(50000);

        paymentUseCase.preparePayment(new PaymentPrepareCommand(1L, idempotencyKey, amount));
        portOnePgClientAdapter.registerMockPayment(paymentKey, idempotencyKey, amount, "PAID");

        // when
        PaymentVerifyCommand verifyCommand = new PaymentVerifyCommand(idempotencyKey, paymentKey);
        PaymentResult result = paymentUseCase.verifyAndApprovePayment(verifyCommand);

        // then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.paymentKey()).isEqualTo(paymentKey);
    }

    @Test
    @DisplayName("PG 결제 금액이 주문 금액과 다르면 금액 위변조로 감지하여 결제가 실패(FAILED) 처리되고 예외가 발생한다")
    void verifyAndApprovePaymentForgeryDetection() {
        // given
        String idempotencyKey = UUID.randomUUID().toString();
        String paymentKey = "imp_fake_" + UUID.randomUUID();
        Money originalAmount = Money.wons(50000);
        Money forgedAmount = Money.wons(500); // 100배 할인(?) 조작 시도!

        paymentUseCase.preparePayment(new PaymentPrepareCommand(1L, idempotencyKey, originalAmount));
        // PG사에는 위변조된 500원 결제 결과가 등록됨
        portOnePgClientAdapter.registerMockPayment(paymentKey, idempotencyKey, forgedAmount, "PAID");

        // when & then
        PaymentVerifyCommand verifyCommand = new PaymentVerifyCommand(idempotencyKey, paymentKey);
        assertThatThrownBy(() -> paymentUseCase.verifyAndApprovePayment(verifyCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("위변조 감지");

        // DB 조회 시 FAILED 상태인지 검증
        PaymentResult failedResult = paymentUseCase.getPaymentByIdempotencyKey(idempotencyKey);
        assertThat(failedResult.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(failedResult.failReason()).contains("위변조 감지");
    }
}
