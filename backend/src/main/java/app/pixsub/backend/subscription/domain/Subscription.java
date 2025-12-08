package app.pixsub.backend.subscription.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class Subscription {
    private final Long id;
    private final Long studentId;
    private final Long planId;
    private final SubscriptionStatus status;
    private final LocalDate nextPaymentDate;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Subscription(Long id,
                        Long studentId,
                        Long planId,
                        SubscriptionStatus status,
                        LocalDate nextPaymentDate,
                        Instant createdAt,
                        Instant updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.planId = planId;
        this.status = status;
        this.nextPaymentDate = nextPaymentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Subscription newSubscription(Long studentId,
                                               Long planId,
                                               LocalDate nextPaymentDate) {
        Instant now = Instant.now();
        return new Subscription(
                null,
                studentId,
                planId,
                SubscriptionStatus.ACTIVE,
                nextPaymentDate,
                now,
                now
        );
    }

    public Subscription cancel() {
        return new Subscription(
                id,
                studentId,
                planId,
                SubscriptionStatus.CANCELLED,
                nextPaymentDate,
                createdAt,
                Instant.now()
        );
    }
}
