package app.pixsub.backend.plan.application;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPlansForTrainerServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private ListPlansForTrainerService service;

    @Test
    void listByTrainer_returnsPlansFromRepository() {
        Long trainerId = 10L;

        Plan plan1 = Plan.newPlan(trainerId, "Plan A", 10000, 30);
        Plan plan2 = Plan.newPlan(trainerId, "Plan B", 20000, 60);
        List<Plan> plans = List.of(plan1, plan2);

        // ✅ matches PlanRepositoryJpaAdapter#findByTrainerId
        given(planRepository.findByTrainerId(trainerId)).willReturn(plans);

        List<Plan> result = service.listByTrainer(trainerId);

        assertThat(result).isEqualTo(plans);
        then(planRepository).should().findByTrainerId(trainerId);
        then(planRepository).shouldHaveNoMoreInteractions();
    }
}
