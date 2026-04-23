package app.pixsub.backend.plan.application;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.PageResult;
import org.springframework.stereotype.Service;

@Service
public class ListPlansForTrainerService {
    private final PlanRepository planRepository;

    public ListPlansForTrainerService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public PageResult<Plan> listByTrainer(Long trainerId, int page, int size) {
        return planRepository.findByTrainerId(trainerId, page, size);
    }
}
