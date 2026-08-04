package practice.projecth.application.payment.port;

public interface PgClientPort {
    /**
     * PG사 단건 결제 상세 정보 조회 (금액/상태 검증용)
     */
    PgPaymentInfo getPaymentInfo(String paymentKey);

    /**
     * PG사 결제 취소 요청 (금액 위변조 감지 또는 결제 후속 처리 실패 시 보상)
     */
    void cancelPayment(String paymentKey, String reason);
}
