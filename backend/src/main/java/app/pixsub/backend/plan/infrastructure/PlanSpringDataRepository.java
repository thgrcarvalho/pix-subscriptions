package app.pixsub.backend.plan.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanSpringDataRepository extends JpaRepository<PlanJpaEntity, Long> {
    List<PlanJpaEntity> findByTrainerId(Long trainerId);
}
