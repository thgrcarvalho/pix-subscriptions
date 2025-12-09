package app.pixsub.backend.plan.application;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreatePlanService {
    private final PlanRepository planRepository;
    private final TrainerRepository trainerRepository;

    public CreatePlanService(PlanRepository planRepository, TrainerRepository trainerRepository) {
        this.planRepository = planRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public Plan create(Long trainerId, String name, long amountInCents, int intervalDays) {
        // Ensure trainer exists
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));

        Plan plan = Plan.newPlan(trainerId, name, amountInCents, intervalDays);
        return planRepository.save(plan);
    }
}
