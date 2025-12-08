package app.pixsub.backend.subscription.application;

import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentRepository;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CreateSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final PlanRepository planRepository;

    public CreateSubscriptionService(SubscriptionRepository subscriptionRepository,
                                     PaymentRepository paymentRepository,
                                     StudentRepository studentRepository,
                                     PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public Subscription create(Long studentId, Long planId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        // Invariant: student and plan must belong to same trainer
        if (!student.getTrainerId().equals(plan.getTrainerId())) {
            throw new IllegalArgumentException("Student and Plan must belong to the same trainer");
        }

        LocalDate today = LocalDate.now();
        LocalDate nextPaymentDate = today.plusDays(plan.getIntervalDays());

        Subscription subscription = Subscription.newSubscription(studentId, planId, nextPaymentDate);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // First payment is due today
        Payment payment = Payment.newPending(savedSubscription.getId(), plan.getAmountInCents(), today);
        paymentRepository.save(payment);

        return savedSubscription;
    }
}
