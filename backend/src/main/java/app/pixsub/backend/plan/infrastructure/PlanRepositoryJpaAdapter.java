package app.pixsub.backend.plan.infrastructure;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlanRepositoryJpaAdapter implements PlanRepository {
    private final PlanSpringDataRepository springData;

    public PlanRepositoryJpaAdapter(PlanSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public Plan save(Plan plan) {
        PlanJpaEntity entity = PlanJpaEntity.fromDomain(plan);
        PlanJpaEntity saved = springData.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return springData.findById(id).map(PlanJpaEntity::toDomain);
    }

    @Override
    public List<Plan> findByTrainerId(Long trainerId) {
        return springData.findByTrainerId(trainerId)
                .stream()
                .map(PlanJpaEntity::toDomain)
                .toList();
    }

    @Override
    public PageResult<Plan> findByTrainerId(Long trainerId, int page, int size) {
        Page<PlanJpaEntity> p = springData.findByTrainerId(trainerId, PageRequest.of(page, size));
        return new PageResult<>(p.getContent().stream().map(PlanJpaEntity::toDomain).toList(),
                p.getTotalElements(), p.getTotalPages(), page, size);
    }
}
