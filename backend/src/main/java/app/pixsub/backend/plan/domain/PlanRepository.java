package app.pixsub.backend.plan.domain;

import app.pixsub.backend.shared.PageResult;

import java.util.List;
import java.util.Optional;

public interface PlanRepository {
    Plan save(Plan plan);

    Optional<Plan> findById(Long id);

    List<Plan> findByTrainerId(Long trainerId);

    PageResult<Plan> findByTrainerId(Long trainerId, int page, int size);
}
