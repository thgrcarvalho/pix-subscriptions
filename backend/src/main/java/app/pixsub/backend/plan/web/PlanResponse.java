package app.pixsub.backend.plan.web;

import lombok.Getter;

import java.time.Instant;

@Getter
public class PlanResponse {
    private Long id;
    private Long trainerId;
    private String name;
    private long amountInCents;
    private int intervalDays;
    private Instant createdAt;

    public PlanResponse(Long id, Long trainerId, String name,
                        long amountInCents, int intervalDays, Instant createdAt) {
        this.id = id;
        this.trainerId = trainerId;
        this.name = name;
        this.amountInCents = amountInCents;
        this.intervalDays = intervalDays;
        this.createdAt = createdAt;
    }
}
