package app.pixsub.backend.plan.web;

import app.pixsub.backend.plan.application.CreatePlanService;
import app.pixsub.backend.plan.application.ListPlansForTrainerService;
import app.pixsub.backend.plan.domain.Plan;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final CreatePlanService createPlanService;
    private final ListPlansForTrainerService listPlansForTrainerService;

    public PlanController(CreatePlanService createPlanService,
                          ListPlansForTrainerService listPlansForTrainerService) {
        this.createPlanService = createPlanService;
        this.listPlansForTrainerService = listPlansForTrainerService;
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody PlanCreateRequest request) {
        Plan plan = createPlanService.create(
                request.getTrainerId(),
                request.getName(),
                request.getAmountInCents(),
                request.getIntervalDays()
        );

        PlanResponse response = toResponse(plan);
        return ResponseEntity
                .created(URI.create("/api/plans/" + plan.getId()))
                .body(response);
    }

    @GetMapping
    public List<PlanResponse> listByTrainer(@RequestParam("trainerId") Long trainerId) {
        return listPlansForTrainerService.listByTrainer(trainerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private PlanResponse toResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getTrainerId(),
                plan.getName(),
                plan.getAmountInCents(),
                plan.getIntervalDays(),
                plan.getCreatedAt()
        );
    }
}
