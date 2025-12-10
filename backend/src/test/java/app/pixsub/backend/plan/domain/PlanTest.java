package app.pixsub.backend.plan.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PlanTest {
    @Test
    void newPlan_initializesWithNullIdAndTimestamps() {
        Long trainerId = 100L;
        String name = "Mensal 100";
        long amountInCents = 100_00L;
        int intervalDays = 30;

        Plan plan = Plan.newPlan(trainerId, name, amountInCents, intervalDays);

        // id null before persistence
        assertThat(plan.getId()).isNull();

        // fields mapped
        assertThat(plan.getTrainerId()).isEqualTo(trainerId);
        assertThat(plan.getName()).isEqualTo(name);
        assertThat(plan.getAmountInCents()).isEqualTo(amountInCents);
        assertThat(plan.getIntervalDays()).isEqualTo(intervalDays);

        // timestamps set
        assertThat(plan.getCreatedAt()).isNotNull();
        assertThat(plan.getUpdatedAt()).isNotNull();
        assertThat(plan.getCreatedAt())
                .isBeforeOrEqualTo(plan.getUpdatedAt());
    }

    @Test
    void rename_returnsNewInstanceWithNewNameAndUpdatedTimestamp() {
        Instant createdAt = Instant.now().minusSeconds(300);
        Instant updatedAt = Instant.now().minusSeconds(200);

        Plan original = new Plan(
                10L,
                100L,
                "Mensal 100",
                100_00L,
                30,
                createdAt,
                updatedAt
        );

        Plan renamed = original.rename("Mensal 150");

        // original unchanged
        assertThat(original.getName()).isEqualTo("Mensal 100");

        // new instance
        assertThat(renamed).isNotSameAs(original);

        // core fields preserved
        assertThat(renamed.getId()).isEqualTo(original.getId());
        assertThat(renamed.getTrainerId()).isEqualTo(original.getTrainerId());
        assertThat(renamed.getAmountInCents()).isEqualTo(original.getAmountInCents());
        assertThat(renamed.getIntervalDays()).isEqualTo(original.getIntervalDays());
        assertThat(renamed.getCreatedAt()).isEqualTo(original.getCreatedAt());

        // name updated + updatedAt refreshed
        assertThat(renamed.getName()).isEqualTo("Mensal 150");
        assertThat(renamed.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }
}
