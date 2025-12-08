package app.pixsub.backend.plan.application;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPlansForTrainerService {
    private final PlanRepository planRepository;

    public ListPlansForTrainerService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<Plan> listByTrainer(Long trainerId) {
        return planRepository.findByTrainerId(trainerId);
    }
}
