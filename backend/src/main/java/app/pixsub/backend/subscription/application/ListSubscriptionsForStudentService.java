package app.pixsub.backend.subscription.application;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class ListSubscriptionsForStudentService {
    private final SubscriptionRepository subscriptionRepository;

    public ListSubscriptionsForStudentService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public PageResult<Subscription> listByStudent(Long studentId, int page, int size) {
        return subscriptionRepository.findByStudentId(studentId, page, size);
    }
}
