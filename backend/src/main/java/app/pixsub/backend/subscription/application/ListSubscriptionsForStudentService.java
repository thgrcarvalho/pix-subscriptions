package app.pixsub.backend.subscription.application;

import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSubscriptionsForStudentService {
    private final SubscriptionRepository subscriptionRepository;

    public ListSubscriptionsForStudentService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Subscription> listByStudent(Long studentId) {
        return subscriptionRepository.findByStudentId(studentId);
    }
}
