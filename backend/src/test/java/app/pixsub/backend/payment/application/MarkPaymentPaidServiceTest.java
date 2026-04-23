package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.shared.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkPaymentPaidServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private MarkPaymentPaidService service;

    @Test
    void markPaid_whenPaymentNotFound_throwsResourceNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markPaid(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void markPaid_whenPending_updatesStatusAndPaidDate() {
        Payment existing = new Payment(
                10L,
                5L,
                100_00L,
                LocalDate.now(),
                null,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                null,
                Instant.now().minusSeconds(60),
                Instant.now().minusSeconds(60)
        );

        when(paymentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Payment updated = service.markPaid(10L);

        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(updated.getPaidDate()).isNotNull();

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();

        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PAID);
    }
}
