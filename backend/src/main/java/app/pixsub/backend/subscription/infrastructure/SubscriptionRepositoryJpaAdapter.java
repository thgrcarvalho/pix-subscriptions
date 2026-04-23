package app.pixsub.backend.subscription.infrastructure;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryJpaAdapter implements SubscriptionRepository {
    private final SubscriptionSpringDataRepository springData;

    public SubscriptionRepositoryJpaAdapter(SubscriptionSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionJpaEntity entity = SubscriptionJpaEntity.fromDomain(subscription);
        SubscriptionJpaEntity saved = springData.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return springData.findById(id).map(SubscriptionJpaEntity::toDomain);
    }

    @Override
    public List<Subscription> findByStudentId(Long studentId) {
        return springData.findByStudentId(studentId)
                .stream()
                .map(SubscriptionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public PageResult<Subscription> findByStudentId(Long studentId, int page, int size) {
        Page<SubscriptionJpaEntity> p = springData.findByStudentId(studentId, PageRequest.of(page, size));
        return new PageResult<>(p.getContent().stream().map(SubscriptionJpaEntity::toDomain).toList(),
                p.getTotalElements(), p.getTotalPages(), page, size);
    }

    @Override
    public List<Subscription> findDueForBilling(LocalDate asOf) {
        return springData.findDueForBilling(asOf)
                .stream()
                .map(SubscriptionJpaEntity::toDomain)
                .toList();
    }
}
