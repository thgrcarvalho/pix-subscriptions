package app.pixsub.backend.subscription.web;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import app.pixsub.backend.test.WebMvcTestSecurityConfig;
import app.pixsub.backend.subscription.application.CreateSubscriptionService;
import app.pixsub.backend.subscription.application.ListSubscriptionsForStudentService;
import app.pixsub.backend.subscription.domain.Subscription;
import app.pixsub.backend.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
@Import({GlobalExceptionHandler.class, WebMvcTestSecurityConfig.class})
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSubscriptionService createSubscriptionService;

    @MockitoBean
    private ListSubscriptionsForStudentService listSubscriptionsForStudentService;

    @Test
    void create_whenValidRequest_returns201AndBodyAndLocation() throws Exception {
        long studentId = 7L;
        long planId = 3L;
        LocalDate nextPaymentDate = LocalDate.parse("2025-02-01");
        Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");

        Subscription created = new Subscription(
                99L,
                studentId,
                planId,
                SubscriptionStatus.ACTIVE,
                nextPaymentDate,
                createdAt,
                createdAt
        );

        given(createSubscriptionService.create(studentId, planId))
                .willReturn(created);

        String body = """
                {
                  "studentId": 7,
                  "planId": 3
                }
                """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/subscriptions/99"))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.studentId").value(7))
                .andExpect(jsonPath("$.planId").value(3))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.nextPaymentDate").value("2025-02-01"));

        verify(createSubscriptionService).create(studentId, planId);
    }

    @Test
    void create_whenMissingStudentId_returns400AndDoesNotCallService() throws Exception {
        String body = """
                {
                  "planId": 3
                }
                """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(createSubscriptionService, never())
                .create(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void create_whenDomainValidationException_returns422FromGlobalHandler() throws Exception {
        long studentId = 7L;
        long planId = 3L;

        given(createSubscriptionService.create(studentId, planId))
                .willThrow(new DomainValidationException("Student and Plan must belong to the same trainer"));

        String body = """
                {
                  "studentId": 7,
                  "planId": 3
                }
                """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Student and Plan must belong to the same trainer"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.path").value("/api/subscriptions"));
    }

    @Test
    void listByStudent_returnsSubscriptionsFromService() throws Exception {
        long studentId = 7L;

        Instant ts1 = Instant.parse("2025-01-01T10:00:00Z");
        Instant ts2 = Instant.parse("2025-03-01T10:00:00Z");
        LocalDate np1 = LocalDate.parse("2025-02-01");
        LocalDate np2 = LocalDate.parse("2025-04-01");

        Subscription s1 = new Subscription(1L, studentId, 3L, SubscriptionStatus.ACTIVE, np1, ts1, ts1);
        Subscription s2 = new Subscription(2L, studentId, 4L, SubscriptionStatus.CANCELLED, np2, ts2, ts2);

        given(listSubscriptionsForStudentService.listByStudent(studentId, 0, 20))
                .willReturn(new PageResult<>(List.of(s1, s2), 2, 1, 0, 20));

        mockMvc.perform(get("/api/subscriptions")
                        .param("studentId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].planId").value(3))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].planId").value(4))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(listSubscriptionsForStudentService).listByStudent(eq(studentId), eq(0), eq(20));
    }
}
