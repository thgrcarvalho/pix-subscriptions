package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.shared.PageResult;
import org.springframework.stereotype.Service;

@Service
public class ListPaymentsForSubscriptionService {
    private final PaymentRepository paymentRepository;

    public ListPaymentsForSubscriptionService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PageResult<Payment> listBySubscription(Long subscriptionId, int page, int size) {
        return paymentRepository.findBySubscriptionId(subscriptionId, page, size);
    }
}
