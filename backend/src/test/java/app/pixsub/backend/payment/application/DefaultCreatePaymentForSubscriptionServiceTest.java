package app.pixsub.backend.payment.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import app.pixsub.backend.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

class DefaultCreatePaymentForSubscriptionServiceTest {
    private final SubscriptionRepository subscriptionRepository = Mockito.mock(SubscriptionRepository.class);
    private final PlanRepository planRepository = Mockito.mock(PlanRepository.class);
    private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private final PixGateway pixGateway = Mockito.mock(PixGateway.class);

    private final DefaultCreatePaymentForSubscriptionService service =
            new DefaultCreatePaymentForSubscriptionService(
                    subscriptionRepository, planRepository, paymentRepository, pixGateway
            );

    @Test
    void create_generatesPixAndPersistsPixFields() {
        // arrange
        Long subscriptionId = 10L;
        Long planId = 30L;

        LocalDate nextPaymentDate = LocalDate.parse("2025-12-20");
        Instant now = Instant.parse("2025-12-11T03:00:00Z");

        Subscription subscription = new Subscription(
                subscriptionId,
                20L, // studentId
                planId,
                SubscriptionStatus.ACTIVE,
                nextPaymentDate,
                now,
                now
        );

        Plan plan = new Plan(
                planId,
                99L, // trainerId
                "Plan A",
                10000L,
                30,
                now,
                now
        );

        given(subscriptionRepository.findById(subscriptionId)).willReturn(Optional.of(subscription));
        given(planRepository.findById(planId)).willReturn(Optional.of(plan));

        PixCharge charge = new PixCharge("QR_10000", "COPYPASTE_123", "provider-123");
        given(pixGateway.createCharge(eq(10000L), anyString())).willReturn(charge);

        // Make save() behave like a DB:
        // 1st save: assigns ID 999
        // 2nd save: returns the passed entity (already with pix data)
        AtomicInteger saveCalls = new AtomicInteger(0);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> {
            Payment arg = invocation.getArgument(0);
            if (saveCalls.incrementAndGet() == 1) {
                return new Payment(
                        999L, // <- simulate DB generated ID
                        arg.getSubscriptionId(),
                        arg.getAmountInCents(),
                        arg.getDueDate(),
                        arg.getPaidDate(),
                        arg.getStatus(),
                        arg.getPixQrCode(),
                        arg.getPixCopyPaste(),
                        arg.getPixProviderPaymentId(),
                        arg.getCreatedAt(),
                        arg.getUpdatedAt()
                );
            }
            return arg;
        });

        // act
        Payment result = service.create(subscriptionId);

        // assert (result contains pix data and provider id)
        assertEquals(subscriptionId, result.getSubscriptionId());
        assertEquals(10000L, result.getAmountInCents());
        assertEquals(nextPaymentDate, result.getDueDate());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals("QR_10000", result.getPixQrCode());
        assertEquals("COPYPASTE_123", result.getPixCopyPaste());
        assertEquals("provider-123", result.getPixProviderPaymentId());

        // verify interaction order + arguments
        InOrder inOrder = inOrder(subscriptionRepository, planRepository, paymentRepository, pixGateway);

        inOrder.verify(subscriptionRepository).findById(subscriptionId);
        inOrder.verify(planRepository).findById(planId);

        ArgumentCaptor<Payment> firstSaveCaptor = ArgumentCaptor.forClass(Payment.class);
        inOrder.verify(paymentRepository).save(firstSaveCaptor.capture());

        Payment firstSavedArg = firstSaveCaptor.getValue();
        assertNull(firstSavedArg.getId()); // before DB assigns it
        assertEquals(subscriptionId, firstSavedArg.getSubscriptionId());
        assertEquals(10000L, firstSavedArg.getAmountInCents());
        assertEquals(nextPaymentDate, firstSavedArg.getDueDate());
        assertEquals(PaymentStatus.PENDING, firstSavedArg.getStatus());

        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        inOrder.verify(pixGateway).createCharge(eq(10000L), descriptionCaptor.capture());
        // Optional: ensure description mentions the generated ID
        assertTrue(descriptionCaptor.getValue().contains("999"));

        ArgumentCaptor<Payment> secondSaveCaptor = ArgumentCaptor.forClass(Payment.class);
        inOrder.verify(paymentRepository).save(secondSaveCaptor.capture());

        Payment secondSavedArg = secondSaveCaptor.getValue();
        assertEquals(999L, secondSavedArg.getId());
        assertEquals("QR_10000", secondSavedArg.getPixQrCode());
        assertEquals("COPYPASTE_123", secondSavedArg.getPixCopyPaste());
        assertEquals("provider-123", secondSavedArg.getPixProviderPaymentId());

        inOrder.verifyNoMoreInteractions();
    }


}
