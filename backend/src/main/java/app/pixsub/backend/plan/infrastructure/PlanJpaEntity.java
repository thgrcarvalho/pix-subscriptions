package app.pixsub.backend.plan.infrastructure;

import app.pixsub.backend.plan.domain.Plan;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "plan", uniqueConstraints = {
        @UniqueConstraint(name = "uq_plan_trainer_name", columnNames = {"trainer_id", "name"})
})
public class PlanJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(nullable = false)
    private String name;

    @Column(name = "amount_in_cents", nullable = false)
    private long amountInCents;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PlanJpaEntity() {
    }

    public static PlanJpaEntity fromDomain(Plan plan) {
        PlanJpaEntity e = new PlanJpaEntity();
        e.id = plan.getId();
        e.trainerId = plan.getTrainerId();
        e.name = plan.getName();
        e.amountInCents = plan.getAmountInCents();
        e.intervalDays = plan.getIntervalDays();
        e.createdAt = plan.getCreatedAt();
        e.updatedAt = plan.getUpdatedAt();
        return e;
    }

    public Plan toDomain() {
        return new Plan(
                id,
                trainerId,
                name,
                amountInCents,
                intervalDays,
                createdAt,
                updatedAt
        );
    }
}
