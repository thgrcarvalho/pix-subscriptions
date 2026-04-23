package app.pixsub.backend.subscription.domain;

import app.pixsub.backend.shared.PageResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long id);

    List<Subscription> findByStudentId(Long studentId);

    PageResult<Subscription> findByStudentId(Long studentId, int page, int size);

    List<Subscription> findDueForBilling(LocalDate asOf);
}
