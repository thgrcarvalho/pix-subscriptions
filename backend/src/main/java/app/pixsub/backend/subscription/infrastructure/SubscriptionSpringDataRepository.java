package app.pixsub.backend.subscription.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionSpringDataRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
    List<SubscriptionJpaEntity> findByStudentId(Long studentId);

    @Query("SELECT s FROM SubscriptionJpaEntity s WHERE s.status = 'ACTIVE' AND s.nextPaymentDate <= :asOf")
    List<SubscriptionJpaEntity> findDueForBilling(LocalDate asOf);
}
