package app.pixsub.backend.subscription.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionSpringDataRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
    List<SubscriptionJpaEntity> findByStudentId(Long studentId);
}
