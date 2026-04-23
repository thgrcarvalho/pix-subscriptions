package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.shared.AuditLogger;
import app.pixsub.backend.shared.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class MarkPaymentPaidService {
    private final PaymentRepository paymentRepository;
    private final AuditLogger auditLogger;

    public MarkPaymentPaidService(PaymentRepository paymentRepository, AuditLogger auditLogger) {
        this.paymentRepository = paymentRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    public Payment markPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return payment;
        }

        Payment updated = payment.markPaid(Instant.now());
        Payment saved = paymentRepository.save(updated);

        auditLogger.log("payment.paid", Map.of(
                "paymentId", saved.getId(),
                "subscriptionId", saved.getSubscriptionId(),
                "amountInCents", saved.getAmountInCents(),
                "provider", saved.getPixProvider() != null ? saved.getPixProvider() : "manual"
        ));

        return saved;
    }
}
