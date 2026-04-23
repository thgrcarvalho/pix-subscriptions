package app.pixsub.backend.payment.domain;

import app.pixsub.backend.shared.PageResult;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findBySubscriptionId(Long subscriptionId);

    PageResult<Payment> findBySubscriptionId(Long subscriptionId, int page, int size);

    Optional<Payment> findByPixProviderPaymentId(String providerPaymentId);
}
