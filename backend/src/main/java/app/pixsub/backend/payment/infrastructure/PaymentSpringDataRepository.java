package app.pixsub.backend.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentSpringDataRepository extends JpaRepository<PaymentJpaEntity, Long> {
    List<PaymentJpaEntity> findBySubscriptionId(Long subscriptionId);
}
