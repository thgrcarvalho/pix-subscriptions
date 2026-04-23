package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;

public interface CreatePaymentForSubscriptionService {
    Payment create(Long subscriptionId);
}
