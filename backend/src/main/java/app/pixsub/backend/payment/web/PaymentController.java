package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.application.ListPaymentsForSubscriptionService;
import app.pixsub.backend.payment.application.MarkPaymentPaidService;
import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.shared.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public PageResult<PaymentResponse> listBySubscription(
            @RequestParam("subscriptionId") Long subscriptionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Payment> result = listPaymentsForSubscriptionService.listBySubscription(subscriptionId, page, size);
        return new PageResult<>(result.content().stream().map(this::toResponse).toList(),
                result.totalElements(), result.totalPages(), result.page(), result.size());
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
                p.getPixProvider(),
                p.getPixQrCode(),
                p.getPixCopyPaste(),
                p.getPixProviderPaymentId(),
                p.getCreatedAt()
        );
    }
}
