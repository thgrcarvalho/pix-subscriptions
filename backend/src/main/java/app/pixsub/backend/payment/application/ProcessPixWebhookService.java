package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessPixWebhookService {

    private static final Logger log = LoggerFactory.getLogger(ProcessPixWebhookService.class);

    private final PaymentRepository paymentRepository;
    private final MarkPaymentPaidService markPaymentPaidService;

    public ProcessPixWebhookService(PaymentRepository paymentRepository,
                                    MarkPaymentPaidService markPaymentPaidService) {
        this.paymentRepository = paymentRepository;
        this.markPaymentPaidService = markPaymentPaidService;
    }

    @Transactional
    public void process(String providerPaymentId) {
        Optional<Payment> payment = paymentRepository.findByPixProviderPaymentId(providerPaymentId);
        if (payment.isEmpty()) {
            // Provider may send webhooks for charges created outside this system — safe to ignore
            log.warn("Webhook received for unknown providerPaymentId: {}", providerPaymentId);
            return;
        }
        markPaymentPaidService.markPaid(payment.get().getId());
        log.info("Payment {} marked paid via webhook (providerPaymentId={})",
                payment.get().getId(), providerPaymentId);
    }
}
