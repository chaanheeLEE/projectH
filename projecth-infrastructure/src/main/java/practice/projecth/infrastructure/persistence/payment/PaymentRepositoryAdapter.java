package practice.projecth.infrastructure.persistence.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.projecth.domain.payment.Payment;
import practice.projecth.domain.payment.PaymentRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentSpringDataJpaRepository springDataJpaRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = PaymentJpaEntity.fromDomain(payment);
        PaymentJpaEntity saved = springDataJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return springDataJpaRepository.findById(id)
                .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return springDataJpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(PaymentJpaEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByPaymentKey(String paymentKey) {
        return springDataJpaRepository.findByPaymentKey(paymentKey)
                .map(PaymentJpaEntity::toDomain);
    }
}
