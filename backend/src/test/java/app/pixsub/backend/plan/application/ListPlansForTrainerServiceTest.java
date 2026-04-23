package app.pixsub.backend.plan.application;

import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.plan.domain.PlanRepository;
import app.pixsub.backend.shared.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListPlansForTrainerServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private ListPlansForTrainerService service;

    @Test
    void listByTrainer_returnsPageResultFromRepository() {
        Long trainerId = 10L;
        int page = 0;
        int size = 20;

        Plan plan1 = Plan.newPlan(trainerId, "Plan A", 10000, 30);
        Plan plan2 = Plan.newPlan(trainerId, "Plan B", 20000, 60);
        PageResult<Plan> expected = new PageResult<>(List.of(plan1, plan2), 2, 1, page, size);

        given(planRepository.findByTrainerId(trainerId, page, size)).willReturn(expected);

        PageResult<Plan> result = service.listByTrainer(trainerId, page, size);

        assertThat(result).isEqualTo(expected);
        then(planRepository).should().findByTrainerId(trainerId, page, size);
        then(planRepository).shouldHaveNoMoreInteractions();
    }
}
