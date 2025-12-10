package app.pixsub.backend.plan.web;

import app.pixsub.backend.plan.application.CreatePlanService;
import app.pixsub.backend.plan.application.ListPlansForTrainerService;
import app.pixsub.backend.plan.domain.Plan;
import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
@Import(GlobalExceptionHandler.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePlanService createPlanService;

    @MockitoBean
    private ListPlansForTrainerService listPlansForTrainerService;

    @Test
    void create_whenValidRequest_returns201AndBodyAndLocationHeader() throws Exception {
        // given
        long trainerId = 10L;
        String name = "Monthly Plan";
        long amountInCents = 10000L;
        int intervalDays = 30;

        Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
        Plan created = new Plan(
                42L,
                trainerId,
                name,
                amountInCents,
                intervalDays,
                createdAt,
                createdAt
        );

        given(createPlanService.create(trainerId, name, amountInCents, intervalDays))
                .willReturn(created);

        String body = """
                {
                  "trainerId": 10,
                  "name": "Monthly Plan",
                  "amountInCents": 10000,
                  "intervalDays": 30
                }
                """;

        // when / then
        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/plans/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.trainerId").value(10))
                .andExpect(jsonPath("$.name").value("Monthly Plan"))
                .andExpect(jsonPath("$.amountInCents").value(10000))
                .andExpect(jsonPath("$.intervalDays").value(30));

        verify(createPlanService).create(trainerId, name, amountInCents, intervalDays);
    }

    @Test
    void create_whenInvalidBody_returns400AndDoesNotCallService() throws Exception {
        // invalid: missing name and intervalDays <= 0
        String body = """
                {
                  "trainerId": 10,
                  "amountInCents": 10000,
                  "intervalDays": 0
                }
                """;

        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(createPlanService, never())
                .create(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void create_whenDomainValidationException_returns422FromGlobalHandler() throws Exception {
        long trainerId = 10L;
        String name = "Monthly Plan";
        long amountInCents = 10000L;
        int intervalDays = 30;

        given(createPlanService.create(trainerId, name, amountInCents, intervalDays))
                .willThrow(new DomainValidationException("Trainer cannot create more plans"));

        String body = """
                {
                  "trainerId": 10,
                  "name": "Monthly Plan",
                  "amountInCents": 10000,
                  "intervalDays": 30
                }
                """;

        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Trainer cannot create more plans"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.path").value("/api/plans"));
    }

    @Test
    void listByTrainer_returnsPlansFromService() throws Exception {
        long trainerId = 10L;

        Instant ts1 = Instant.parse("2025-01-01T10:00:00Z");
        Instant ts2 = Instant.parse("2025-01-02T11:00:00Z");

        Plan p1 = new Plan(1L, trainerId, "Monthly", 10000L, 30, ts1, ts1);
        Plan p2 = new Plan(2L, trainerId, "Yearly", 100000L, 365, ts2, ts2);

        given(listPlansForTrainerService.listByTrainer(trainerId))
                .willReturn(java.util.List.of(p1, p2));

        mockMvc.perform(get("/api/plans")
                        .param("trainerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Monthly"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Yearly"));

        verify(listPlansForTrainerService).listByTrainer(eq(trainerId));
    }
}
