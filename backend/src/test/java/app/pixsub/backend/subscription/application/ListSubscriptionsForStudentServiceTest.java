package app.pixsub.backend.subscription.application;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionRepository;
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
class ListSubscriptionsForStudentServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private ListSubscriptionsForStudentService service;

    @Test
    void listByStudent_returnsPageResultFromRepository() {
        Long studentId = 5L;
        Long planId = 99L;
        int page = 0;
        int size = 20;

        Subscription s1 = Subscription.newSubscription(studentId, planId, LocalDate.now());
        Subscription s2 = Subscription.newSubscription(studentId, planId, LocalDate.now().plusDays(30));
        PageResult<Subscription> expected = new PageResult<>(List.of(s1, s2), 2, 1, page, size);

        given(subscriptionRepository.findByStudentId(studentId, page, size)).willReturn(expected);

        PageResult<Subscription> result = service.listByStudent(studentId, page, size);

        assertThat(result).isEqualTo(expected);
        then(subscriptionRepository).should().findByStudentId(studentId, page, size);
        then(subscriptionRepository).shouldHaveNoMoreInteractions();
    }
}
