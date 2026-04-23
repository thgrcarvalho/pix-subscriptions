package app.pixsub.backend.billing;

import app.pixsub.backend.payment.application.CreatePaymentForSubscriptionService;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import app.pixsub.backend.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingSchedulerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CreatePaymentForSubscriptionService createPaymentService;

    @InjectMocks
    private BillingScheduler scheduler;

    @Test
    void billDueSubscriptions_whenNoneDue_doesNotCreatePayments() {
        given(subscriptionRepository.findDueForBilling(any(LocalDate.class))).willReturn(List.of());

        scheduler.billDueSubscriptions();

        verify(createPaymentService, never()).create(any());
    }

    @Test
    void billDueSubscriptions_createsPaymentForEachDueSubscription() {
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        Subscription s1 = new Subscription(1L, 10L, 20L, SubscriptionStatus.ACTIVE, today, now, now);
        Subscription s2 = new Subscription(2L, 11L, 21L, SubscriptionStatus.ACTIVE, today.minusDays(1), now, now);

        given(subscriptionRepository.findDueForBilling(any(LocalDate.class))).willReturn(List.of(s1, s2));

        scheduler.billDueSubscriptions();

        verify(createPaymentService).create(1L);
        verify(createPaymentService).create(2L);
        verifyNoMoreInteractions(createPaymentService);
    }

    @Test
    void billDueSubscriptions_whenOneFailsOtherStillProcessed() {
        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        Subscription s1 = new Subscription(1L, 10L, 20L, SubscriptionStatus.ACTIVE, today, now, now);
        Subscription s2 = new Subscription(2L, 11L, 21L, SubscriptionStatus.ACTIVE, today, now, now);

        given(subscriptionRepository.findDueForBilling(any(LocalDate.class))).willReturn(List.of(s1, s2));
        doThrow(new RuntimeException("Pix gateway timeout")).when(createPaymentService).create(1L);

        scheduler.billDueSubscriptions();

        verify(createPaymentService).create(1L);
        verify(createPaymentService).create(2L);
    }
}
