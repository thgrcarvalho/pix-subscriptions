package app.pixsub.backend.subscription.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionTest {
    @Test
    void newSubscription_initializesActiveWithNullIdAndTimestamps() {
        Long studentId = 1L;
        Long planId = 10L;
        LocalDate nextPaymentDate = LocalDate.of(2025, 1, 15);

        Subscription subscription = Subscription.newSubscription(studentId, planId, nextPaymentDate);

        // id is null before persistence
        assertThat(subscription.getId()).isNull();

        // fields mapped correctly
        assertThat(subscription.getStudentId()).isEqualTo(studentId);
        assertThat(subscription.getPlanId()).isEqualTo(planId);
        assertThat(subscription.getNextPaymentDate()).isEqualTo(nextPaymentDate);

        // status initialized as ACTIVE
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

        // timestamps initialized
        assertThat(subscription.getCreatedAt()).isNotNull();
        assertThat(subscription.getUpdatedAt()).isNotNull();
        assertThat(subscription.getCreatedAt())
                .isBeforeOrEqualTo(subscription.getUpdatedAt());
    }

    @Test
    void cancel_returnsNewInstanceWithCancelledStatus_preservingOtherFields() {
        Long studentId = 1L;
        Long planId = 10L;
        LocalDate nextPaymentDate = LocalDate.of(2025, 1, 15);
        Instant createdAt = Instant.now().minusSeconds(300);
        Instant updatedAt = Instant.now().minusSeconds(200);

        Subscription original = new Subscription(
                123L,
                studentId,
                planId,
                SubscriptionStatus.ACTIVE,
                nextPaymentDate,
                createdAt,
                updatedAt
        );

        Subscription cancelled = original.cancel();

        // original unchanged
        assertThat(original.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

        // new instance
        assertThat(cancelled).isNotSameAs(original);

        // identity & core fields preserved
        assertThat(cancelled.getId()).isEqualTo(original.getId());
        assertThat(cancelled.getStudentId()).isEqualTo(original.getStudentId());
        assertThat(cancelled.getPlanId()).isEqualTo(original.getPlanId());
        assertThat(cancelled.getNextPaymentDate()).isEqualTo(original.getNextPaymentDate());
        assertThat(cancelled.getCreatedAt()).isEqualTo(original.getCreatedAt());

        // status changed
        assertThat(cancelled.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);

        // updatedAt refreshed
        assertThat(cancelled.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }
}
