package practice.projecth.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentSpringDataJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    Optional<PaymentJpaEntity> findByIdempotencyKey(String idempotencyKey);
    Optional<PaymentJpaEntity> findByPaymentKey(String paymentKey);
}
