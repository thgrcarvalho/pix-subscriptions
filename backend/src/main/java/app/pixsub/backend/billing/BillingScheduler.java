package app.pixsub.backend.billing;

import app.pixsub.backend.payment.application.CreatePaymentForSubscriptionService;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BillingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BillingScheduler.class);

    private final SubscriptionRepository subscriptionRepository;
    private final CreatePaymentForSubscriptionService createPaymentService;

    public BillingScheduler(SubscriptionRepository subscriptionRepository,
                            CreatePaymentForSubscriptionService createPaymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.createPaymentService = createPaymentService;
    }

    // Runs every day at 08:00
    @Scheduled(cron = "0 0 8 * * *")
    public void billDueSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> due = subscriptionRepository.findDueForBilling(today);

        if (due.isEmpty()) {
            log.info("Billing run for {}: no subscriptions due", today);
            return;
        }

        log.info("Billing run for {}: {} subscription(s) due", today, due.size());

        int succeeded = 0;
        int failed = 0;
        for (Subscription subscription : due) {
            try {
                createPaymentService.create(subscription.getId());
                succeeded++;
            } catch (Exception e) {
                // Log and continue — one failure must not abort the entire billing run
                log.error("Failed to create payment for subscription {}: {}", subscription.getId(), e.getMessage(), e);
                failed++;
            }
        }

        log.info("Billing run for {} complete: {} succeeded, {} failed", today, succeeded, failed);
    }
}
