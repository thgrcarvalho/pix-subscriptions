package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.domain.PaymentStatus;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class PaymentResponse {
    private Long id;
    private Long subscriptionId;
    private long amountInCents;
    private LocalDate dueDate;
    private Instant paidDate;
    private PaymentStatus status;
    private String pixQrCode;
    private String pixCopyPaste;
    private String pixProviderPaymentId;
    private Instant createdAt;

    public PaymentResponse(Long id,
                           Long subscriptionId,
                           long amountInCents,
                           LocalDate dueDate,
                           Instant paidDate,
                           PaymentStatus status,
                           String pixQrCode,
                           String pixCopyPaste,
                           String pixProviderPaymentId,
                           Instant createdAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.amountInCents = amountInCents;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.status = status;
        this.pixQrCode = pixQrCode;
        this.pixCopyPaste = pixCopyPaste;
        this.pixProviderPaymentId = pixProviderPaymentId;
        this.createdAt = createdAt;
    }
}
