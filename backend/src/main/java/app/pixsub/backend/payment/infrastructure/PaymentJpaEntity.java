package app.pixsub.backend.payment.infrastructure;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class PaymentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "amount_in_cents", nullable = false)
    private long amountInCents;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private Instant paidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "pix_provider")
    private String pixProvider;

    @Column(name = "pix_qr_code")
    private String pixQrCode;

    @Column(name = "pix_copy_paste")
    private String pixCopyPaste;

    @Column(name = "pix_payment_id")
    private String pixProviderPaymentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PaymentJpaEntity() {
    }

    public static PaymentJpaEntity fromDomain(Payment p) {
        PaymentJpaEntity e = new PaymentJpaEntity();
        e.id = p.getId();
        e.subscriptionId = p.getSubscriptionId();
        e.amountInCents = p.getAmountInCents();
        e.dueDate = p.getDueDate();
        e.paidDate = p.getPaidDate();
        e.status = p.getStatus();
        e.pixProvider = p.getPixProvider();
        e.pixQrCode = p.getPixQrCode();
        e.pixCopyPaste = p.getPixCopyPaste();
        e.pixProviderPaymentId = p.getPixProviderPaymentId();
        e.createdAt = p.getCreatedAt();
        e.updatedAt = p.getUpdatedAt();
        return e;
    }

    public Payment toDomain() {
        return new Payment(
                id,
                subscriptionId,
                amountInCents,
                dueDate,
                paidDate,
                status,
                pixProvider,
                pixQrCode,
                pixCopyPaste,
                pixProviderPaymentId,
                createdAt,
                updatedAt
        );
    }
}
