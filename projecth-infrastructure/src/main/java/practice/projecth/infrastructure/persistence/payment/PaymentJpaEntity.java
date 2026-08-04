package practice.projecth.infrastructure.persistence.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.payment.Payment;
import practice.projecth.domain.payment.PaymentStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_idempotency_key", columnList = "idempotencyKey", unique = true),
        @Index(name = "idx_payment_payment_key", columnList = "paymentKey")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column
    private String paymentKey;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column
    private String failReason;

    @Version
    private Long version;

    public PaymentJpaEntity(Long id, Long orderId, String idempotencyKey, String paymentKey, BigDecimal amount, PaymentStatus status, String failReason, Long version) {
        this.id = id;
        this.orderId = orderId;
        this.idempotencyKey = idempotencyKey;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.failReason = failReason;
        this.version = version;
    }

    public static PaymentJpaEntity fromDomain(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId(),
                payment.getOrderId(),
                payment.getIdempotencyKey(),
                payment.getPaymentKey(),
                payment.getAmount().getAmount(),
                payment.getStatus(),
                payment.getFailReason(),
                payment.getVersion()
        );
    }

    public Payment toDomain() {
        return new Payment(
                id,
                orderId,
                idempotencyKey,
                paymentKey,
                Money.wons(amount),
                status,
                failReason,
                version
        );
    }
}
