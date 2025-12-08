package app.pixsub.backend.subscription.infrastructure;

import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "subscription")
public class SubscriptionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SubscriptionJpaEntity() {
    }

    public static SubscriptionJpaEntity fromDomain(Subscription s) {
        SubscriptionJpaEntity e = new SubscriptionJpaEntity();
        e.id = s.getId();
        e.studentId = s.getStudentId();
        e.planId = s.getPlanId();
        e.status = s.getStatus();
        e.nextPaymentDate = s.getNextPaymentDate();
        e.createdAt = s.getCreatedAt();
        e.updatedAt = s.getUpdatedAt();
        return e;
    }

    public Subscription toDomain() {
        return new Subscription(
                id,
                studentId,
                planId,
                status,
                nextPaymentDate,
                createdAt,
                updatedAt
        );
    }
}
