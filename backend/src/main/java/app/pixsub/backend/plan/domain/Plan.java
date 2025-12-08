package app.pixsub.backend.plan.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Plan {

    private final Long id;
    private final Long trainerId;
    private final String name;
    private final long amountInCents;
    private final int intervalDays;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Plan(Long id,
                Long trainerId,
                String name,
                long amountInCents,
                int intervalDays,
                Instant createdAt,
                Instant updatedAt) {
        this.id = id;
        this.trainerId = trainerId;
        this.name = name;
        this.amountInCents = amountInCents;
        this.intervalDays = intervalDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Plan newPlan(Long trainerId,
                               String name,
                               long amountInCents,
                               int intervalDays) {
        Instant now = Instant.now();
        return new Plan(null, trainerId, name, amountInCents, intervalDays, now, now);
    }

    public Plan rename(String newName) {
        return new Plan(id, trainerId, newName, amountInCents, intervalDays, createdAt, Instant.now());
    }
}
