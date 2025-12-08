package app.pixsub.backend.subscription.domain;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long id);

    List<Subscription> findByStudentId(Long studentId);
}
