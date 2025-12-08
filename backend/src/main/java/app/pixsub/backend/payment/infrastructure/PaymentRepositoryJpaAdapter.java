package app.pixsub.backend.payment.infrastructure;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryJpaAdapter implements PaymentRepository {
    private final PaymentSpringDataRepository springData;

    public PaymentRepositoryJpaAdapter(PaymentSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = PaymentJpaEntity.fromDomain(payment);
        PaymentJpaEntity saved = springData.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return springData.findById(id).map(PaymentJpaEntity::toDomain);
    }

    @Override
    public List<Payment> findBySubscriptionId(Long subscriptionId) {
        return springData.findBySubscriptionId(subscriptionId)
                .stream()
                .map(PaymentJpaEntity::toDomain)
                .toList();
    }
}
