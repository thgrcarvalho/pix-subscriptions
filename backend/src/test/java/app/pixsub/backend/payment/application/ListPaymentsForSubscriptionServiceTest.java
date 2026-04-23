package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListPaymentsForSubscriptionServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private ListPaymentsForSubscriptionService service;

    @Test
    void listBySubscription_returnsPageResultFromRepository() {
        Long subscriptionId = 3L;
        int page = 0;
        int size = 20;

        Payment p1 = Payment.newPending(subscriptionId, 1000L, LocalDate.now());
        Payment p2 = Payment.newPending(subscriptionId, 2000L, LocalDate.now().plusDays(30));
        PageResult<Payment> expected = new PageResult<>(List.of(p1, p2), 2, 1, page, size);

        given(paymentRepository.findBySubscriptionId(subscriptionId, page, size)).willReturn(expected);

        PageResult<Payment> result = service.listBySubscription(subscriptionId, page, size);

        assertThat(result).isEqualTo(expected);
        then(paymentRepository).should().findBySubscriptionId(subscriptionId, page, size);
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }
}
