package app.pixsub.backend.plan.web;

import app.pixsub.backend.plan.application.CreatePlanService;
import app.pixsub.backend.plan.application.ListPlansForTrainerService;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.shared.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PageResult<PlanResponse> listByTrainer(
            @RequestParam("trainerId") Long trainerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Plan> result = listPlansForTrainerService.listByTrainer(trainerId, page, size);
        return new PageResult<>(result.content().stream().map(this::toResponse).toList(),
                result.totalElements(), result.totalPages(), result.page(), result.size());
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
