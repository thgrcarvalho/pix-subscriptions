package app.pixsub.backend.subscription.web;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SubscriptionCreateRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long planId;
}
