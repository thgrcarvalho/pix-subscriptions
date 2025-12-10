package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPaymentsForSubscriptionServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ListPaymentsForSubscriptionService service;

    @Test
    void listBySubscription_returnsPaymentsFromRepository() {
        Long subscriptionId = 3L;

        Payment p1 = Payment.newPending(subscriptionId, 1000L, LocalDate.now());
        Payment p2 = Payment.newPending(subscriptionId, 2000L, LocalDate.now().plusDays(30));
        List<Payment> payments = List.of(p1, p2);

        // ✅ matches PaymentRepositoryJpaAdapter#findBySubscriptionId
        given(paymentRepository.findBySubscriptionId(subscriptionId)).willReturn(payments);

        List<Payment> result = service.listBySubscription(subscriptionId);

        assertThat(result).isEqualTo(payments);
        then(paymentRepository).should().findBySubscriptionId(subscriptionId);
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }
}
