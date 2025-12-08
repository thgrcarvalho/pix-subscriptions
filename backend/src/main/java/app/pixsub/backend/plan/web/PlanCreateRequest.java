package app.pixsub.backend.plan.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PlanCreateRequest {
    @NotNull
    private Long trainerId;

    @NotBlank
    private String name;

    @Min(1)
    private long amountInCents;

    @Min(1)
    private int intervalDays;
}
