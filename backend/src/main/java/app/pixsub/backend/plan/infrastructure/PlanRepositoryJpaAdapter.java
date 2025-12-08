package app.pixsub.backend.plan.infrastructure;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
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
}
