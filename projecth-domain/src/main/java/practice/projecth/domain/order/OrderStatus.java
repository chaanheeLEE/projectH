package practice.projecth.domain.order;

public enum OrderStatus {
    PENDING,          // 주문 접수 / 대기
    STOCK_DEDUCTED,   // 재고 차감 완료
    PAYMENT_APPROVED, // 결제 승인 완료
    CANCELLED,        // 주문 취소
    FAILED            // 주문 실패
}
