package app.pixsub.backend.payment.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentSpringDataRepository extends JpaRepository<PaymentJpaEntity, Long> {
    List<PaymentJpaEntity> findBySubscriptionId(Long subscriptionId);

    Page<PaymentJpaEntity> findBySubscriptionId(Long subscriptionId, Pageable pageable);
}
