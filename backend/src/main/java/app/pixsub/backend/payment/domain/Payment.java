package app.pixsub.backend.payment.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class Payment {
    private final Long id;
    private final Long subscriptionId;
    private final long amountInCents;
    private final LocalDate dueDate;
    private final Instant paidDate;
    private final PaymentStatus status;
    private final String pixQrCode;
    private final String pixCopyPaste;
    private final String pixProviderPaymentId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Payment(Long id,
                   Long subscriptionId,
                   long amountInCents,
                   LocalDate dueDate,
                   Instant paidDate,
                   PaymentStatus status,
                   String pixQrCode,
                   String pixCopyPaste,
                   String pixProviderPaymentId,
                   Instant createdAt,
                   Instant updatedAt) {
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
        this.updatedAt = updatedAt;
    }

    public static Payment newPending(Long subscriptionId,
                                     long amountInCents,
                                     LocalDate dueDate) {
        Instant now = Instant.now();
        return new Payment(
                null,
                subscriptionId,
                amountInCents,
                dueDate,
                null,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                now,
                now
        );
    }

    public Payment markPaid(Instant paidAt) {
        return new Payment(
                id,
                subscriptionId,
                amountInCents,
                dueDate,
                paidAt,
                PaymentStatus.PAID,
                pixQrCode,
                pixCopyPaste,
                pixProviderPaymentId,
                createdAt,
                Instant.now()
        );
    }
}
