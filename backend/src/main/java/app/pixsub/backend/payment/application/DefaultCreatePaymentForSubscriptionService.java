package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DefaultCreatePaymentForSubscriptionService implements CreatePaymentForSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final PixGateway pixGateway; // NEW

    public DefaultCreatePaymentForSubscriptionService(SubscriptionRepository subscriptionRepository,
                                                      PlanRepository planRepository,
                                                      PaymentRepository paymentRepository,
                                                      PixGateway pixGateway) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.pixGateway = pixGateway;
    }

    @Override
    @Transactional
    public Payment create(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", subscriptionId));

        Plan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", subscription.getPlanId()));

        Payment pending = Payment.newPending(
                subscriptionId,
                plan.getAmountInCents(),
                subscription.getNextPaymentDate()
        );

        Payment saved = paymentRepository.save(pending);

        String description = "Subscription " + subscriptionId + " payment " + saved.getId();
        PixCharge charge = pixGateway.createCharge(saved.getAmountInCents(), description);

        Payment withPix = saved.withPixData(
                pixGateway.provider(),
                charge.qrCode(),
                charge.copyPaste(),
                charge.providerPaymentId()
        );

        Payment result = paymentRepository.save(withPix);

        subscriptionRepository.save(subscription.advanceNextPaymentDate(plan.getIntervalDays()));

        return result;
    }
}
