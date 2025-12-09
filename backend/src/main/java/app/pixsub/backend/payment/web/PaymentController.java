package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.application.ListPaymentsForSubscriptionService;
import app.pixsub.backend.payment.application.MarkPaymentPaidService;
import app.pixsub.backend.payment.domain.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final ListPaymentsForSubscriptionService listPaymentsForSubscriptionService;
    private final MarkPaymentPaidService markPaymentPaidService;

    public PaymentController(ListPaymentsForSubscriptionService listPaymentsForSubscriptionService,
                             MarkPaymentPaidService markPaymentPaidService) {
        this.listPaymentsForSubscriptionService = listPaymentsForSubscriptionService;
        this.markPaymentPaidService = markPaymentPaidService;
    }

    @GetMapping
    public List<PaymentResponse> listBySubscription(@RequestParam("subscriptionId") Long subscriptionId) {
        return listPaymentsForSubscriptionService.listBySubscription(subscriptionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/{paymentId}/pay")
    public ResponseEntity<PaymentResponse> markPaid(@PathVariable Long paymentId) {
        Payment updated = markPaymentPaidService.markPaid(paymentId);
        return ResponseEntity.ok(toResponse(updated));
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getSubscriptionId(),
                p.getAmountInCents(),
                p.getDueDate(),
                p.getPaidDate(),
                p.getStatus(),
                p.getPixQrCode(),
                p.getPixCopyPaste(),
                p.getPixProviderPaymentId(),
                p.getCreatedAt()
        );
    }
}
