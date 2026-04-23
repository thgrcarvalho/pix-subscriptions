package app.pixsub.backend.payment.infrastructure;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.shared.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public PageResult<Payment> findBySubscriptionId(Long subscriptionId, int page, int size) {
        Page<PaymentJpaEntity> p = springData.findBySubscriptionId(subscriptionId, PageRequest.of(page, size));
        return new PageResult<>(p.getContent().stream().map(PaymentJpaEntity::toDomain).toList(),
                p.getTotalElements(), p.getTotalPages(), page, size);
    }
}
