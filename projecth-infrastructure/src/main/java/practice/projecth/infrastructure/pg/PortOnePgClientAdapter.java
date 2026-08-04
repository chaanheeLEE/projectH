package practice.projecth.infrastructure.pg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import practice.projecth.application.payment.port.PgClientPort;
import practice.projecth.application.payment.port.PgPaymentInfo;
import practice.projecth.domain.common.Money;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PortOnePgClientAdapter implements PgClientPort {

    // 테스트/시뮬레이션을 위한 PG 메모리 저장소
    private final Map<String, PgPaymentInfo> mockPgDatabase = new ConcurrentHashMap<>();

    /**
     * 테스트용: PG사 결제 결과 사전 등록 (정상 승인 또는 금액 위변조 시뮬레이션)
     */
    public void registerMockPayment(String paymentKey, String idempotencyKey, Money amount, String status) {
        mockPgDatabase.put(paymentKey, new PgPaymentInfo(paymentKey, idempotencyKey, amount, status, null));
    }

    @Override
    public PgPaymentInfo getPaymentInfo(String paymentKey) {
        log.info("[PortOne PG API] 단건 결제 조회 요청 - paymentKey: {}", paymentKey);
        
        PgPaymentInfo info = mockPgDatabase.get(paymentKey);
        if (info == null) {
            // 기본 테스트용 응답 (실제 등록되지 않은 키일 경우 정상으로 가정하거나 예외)
            throw new IllegalArgumentException("PG사에서 결제 정보를 찾을 수 없습니다. paymentKey: " + paymentKey);
        }
        return info;
    }

    @Override
    public void cancelPayment(String paymentKey, String reason) {
        log.warn("[PortOne PG API] 결제 취소 요청 - paymentKey: {}, 사유: {}", paymentKey, reason);
        PgPaymentInfo existing = mockPgDatabase.get(paymentKey);
        if (existing != null) {
            mockPgDatabase.put(paymentKey, new PgPaymentInfo(
                    existing.paymentKey(),
                    existing.idempotencyKey(),
                    existing.amount(),
                    "CANCELLED",
                    reason
            ));
        }
    }
}
