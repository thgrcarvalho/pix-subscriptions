package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPaymentsForSubscriptionService {
    private final PaymentRepository paymentRepository;

    public ListPaymentsForSubscriptionService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> listBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }
}
