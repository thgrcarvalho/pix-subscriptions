package app.pixsub.backend.subscription.application;

import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
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
class ListSubscriptionsForStudentServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private ListSubscriptionsForStudentService service;

    @Test
    void listByStudent_returnsSubscriptionsFromRepository() {
        Long studentId = 5L;
        Long planId = 99L;

        Subscription s1 = Subscription.newSubscription(studentId, planId, LocalDate.now());
        Subscription s2 = Subscription.newSubscription(studentId, planId, LocalDate.now().plusDays(30));
        List<Subscription> subs = List.of(s1, s2);

        // ✅ matches SubscriptionRepositoryJpaAdapter#findByStudentId
        given(subscriptionRepository.findByStudentId(studentId)).willReturn(subs);

        List<Subscription> result = service.listByStudent(studentId);

        assertThat(result).isEqualTo(subs);
        then(subscriptionRepository).should().findByStudentId(studentId);
        then(subscriptionRepository).shouldHaveNoMoreInteractions();
    }
}
