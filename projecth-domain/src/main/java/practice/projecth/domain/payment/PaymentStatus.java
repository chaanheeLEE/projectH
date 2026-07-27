package practice.projecth.domain.payment;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum PaymentStatus {
    INITIATED("결제 생성"),
    PG_REQUESTED("PG 승인 요청 중"),
    APPROVED("PG 승인 및 검증 완료"),
    COMPLETED("결제 최종 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소 완료");

    private final String description;
    private Set<PaymentStatus> allowedTransitions;

    PaymentStatus(String description) {
        this.description = description;
    }

    static {
        INITIATED.allowedTransitions = EnumSet.of(PG_REQUESTED, FAILED);
        PG_REQUESTED.allowedTransitions = EnumSet.of(APPROVED, FAILED);
        APPROVED.allowedTransitions = EnumSet.of(COMPLETED, CANCELLED);
        COMPLETED.allowedTransitions = EnumSet.of(CANCELLED);
        FAILED.allowedTransitions = Collections.emptySet();
        CANCELLED.allowedTransitions = Collections.emptySet();
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(PaymentStatus target) {
        return allowedTransitions.contains(target);
    }

    public void verifyTransitionTo(PaymentStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                String.format("결제 상태를 [%s]에서 [%s](으)로 전이할 수 없습니다.", this.name(), target.name())
            );
        }
    }
}
