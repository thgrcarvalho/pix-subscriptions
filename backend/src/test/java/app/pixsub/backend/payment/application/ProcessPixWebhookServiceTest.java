package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.payment.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProcessPixWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MarkPaymentPaidService markPaymentPaidService;

    @InjectMocks
    private ProcessPixWebhookService service;

    @Test
    void process_whenPaymentFound_marksItPaid() {
        String providerPaymentId = "efi-abc123";
        Instant now = Instant.now();
        Payment payment = new Payment(42L, 1L, 10000L, LocalDate.now(), null,
                PaymentStatus.PENDING, "efibank", "QR", "COPY", providerPaymentId, now, now);

        given(paymentRepository.findByPixProviderPaymentId(providerPaymentId))
                .willReturn(Optional.of(payment));

        service.process(providerPaymentId);

        then(markPaymentPaidService).should().markPaid(42L);
    }

    @Test
    void process_whenPaymentNotFound_logsAndIgnores() {
        given(paymentRepository.findByPixProviderPaymentId("unknown-id"))
                .willReturn(Optional.empty());

        service.process("unknown-id");

        then(markPaymentPaidService).should(never()).markPaid(org.mockito.ArgumentMatchers.any());
    }
}
