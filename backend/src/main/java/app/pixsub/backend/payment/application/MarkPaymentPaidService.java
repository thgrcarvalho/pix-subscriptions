package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.shared.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MarkPaymentPaidService {
    private final PaymentRepository paymentRepository;

    public MarkPaymentPaidService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment markPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return payment; // idempotent for now
        }

        Payment updated = payment.markPaid(Instant.now());
        return paymentRepository.save(updated);
    }
}
