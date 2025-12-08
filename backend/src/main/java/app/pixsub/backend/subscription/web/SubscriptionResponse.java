package app.pixsub.backend.subscription.web;

import app.pixsub.backend.subscription.domain.SubscriptionStatus;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class SubscriptionResponse {
    private Long id;
    private Long studentId;
    private Long planId;
    private SubscriptionStatus status;
    private LocalDate nextPaymentDate;
    private Instant createdAt;

    public SubscriptionResponse(Long id,
                                Long studentId,
                                Long planId,
                                SubscriptionStatus status,
                                LocalDate nextPaymentDate,
                                Instant createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.planId = planId;
        this.status = status;
        this.nextPaymentDate = nextPaymentDate;
        this.createdAt = createdAt;
    }
}
