package app.pixsub.backend.subscription.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private CreateSubscriptionService service;

    @Test
    void create_whenStudentOrPlanMissing_throwsResourceNotFound() {
        Long studentId = 1L;
        Long planId = 10L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(studentId, planId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(planRepository, never()).findById(any());
        verify(subscriptionRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void create_whenDifferentTrainers_throwsDomainValidationException() {
        Long studentId = 1L;
        Long planId = 10L;

        Student student = new Student(1L, 100L, "Aluno", "contact", null, Instant.now(), Instant.now());
        Plan plan = new Plan(10L, 999L, "Plan", 10_00L, 30, Instant.now(), Instant.now()); // different trainerId

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.create(studentId, planId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("same trainer");

        verify(subscriptionRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void create_whenValid_createsSubscriptionAndFirstPayment() {
        Long studentId = 1L;
        Long planId = 10L;

        long trainerId = 100L;
        Student student = new Student(studentId, trainerId, "Aluno", "contact", null, Instant.now(), Instant.now());
        Plan plan = new Plan(planId, trainerId, "Mensal 100", 100_00L, 30, Instant.now(), Instant.now());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));

        // echo what we receive, but with generated id for subscription
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> {
            Subscription s = invocation.getArgument(0);
            return new Subscription(
                    123L,
                    s.getStudentId(),
                    s.getPlanId(),
                    s.getStatus(),
                    s.getNextPaymentDate(),
                    s.getCreatedAt(),
                    s.getUpdatedAt()
            );
        });
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription result = service.create(studentId, planId);

        assertThat(result.getId()).isEqualTo(123L);
        assertThat(result.getStudentId()).isEqualTo(studentId);
        assertThat(result.getPlanId()).isEqualTo(planId);

        // verify payment saved with correct subscriptionId and amount
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment payment = paymentCaptor.getValue();

        assertThat(payment.getSubscriptionId()).isEqualTo(123L);
        assertThat(payment.getAmountInCents()).isEqualTo(plan.getAmountInCents());
        assertThat(payment.getDueDate()).isEqualTo(LocalDate.now());
    }
}
