package app.pixsub.backend.subscription.web;

import app.pixsub.backend.payment.application.CreatePaymentForSubscriptionService;
import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.web.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/subscriptions/{subscriptionId}/payments")
public class SubscriptionPaymentController {
    private final CreatePaymentForSubscriptionService createPaymentForSubscriptionService;

    public SubscriptionPaymentController(CreatePaymentForSubscriptionService createPaymentForSubscriptionService) {
        this.createPaymentForSubscriptionService = createPaymentForSubscriptionService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@PathVariable Long subscriptionId) {
        Payment created = createPaymentForSubscriptionService.create(subscriptionId);

        PaymentResponse response = toResponse(created);

        // You can pick either Location style; this one points to the "payment resource"
        return ResponseEntity
                .created(URI.create("/api/payments/" + created.getId()))
                .body(response);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getSubscriptionId(),
                p.getAmountInCents(),
                p.getDueDate(),
                p.getPaidDate(),
                p.getStatus(),
                p.getPixProvider(),
                p.getPixQrCode(),
                p.getPixCopyPaste(),
                p.getPixProviderPaymentId(),
                p.getCreatedAt()
        );
    }
}
