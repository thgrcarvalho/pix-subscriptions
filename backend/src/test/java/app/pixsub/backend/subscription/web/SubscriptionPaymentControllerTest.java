package app.pixsub.backend.subscription.web;

import app.pixsub.backend.payment.application.CreatePaymentForSubscriptionService;
import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubscriptionPaymentController.class)
@Import(GlobalExceptionHandler.class)
public class SubscriptionPaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePaymentForSubscriptionService createPaymentForSubscriptionService;

    @Test
    void create_returns201_body_and_location() throws Exception {
        Long subscriptionId = 10L;
        Instant now = Instant.parse("2025-12-11T03:00:00Z");
        LocalDate due = LocalDate.parse("2025-12-20");

        Payment created = new Payment(
                99L,
                subscriptionId,
                10000L,
                due,
                null,
                PaymentStatus.PENDING,
                "qr",
                "copy",
                "provider-123",
                now,
                now
        );

        given(createPaymentForSubscriptionService.create(eq(subscriptionId))).willReturn(created);

        mockMvc.perform(post("/api/subscriptions/{subscriptionId}/payments", subscriptionId))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/payments/99"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.subscriptionId").value(10))
                .andExpect(jsonPath("$.amountInCents").value(10000))
                .andExpect(jsonPath("$.dueDate").value("2025-12-20"))
                .andExpect(jsonPath("$.paidDate").doesNotExist())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.pixQrCode").value("qr"))
                .andExpect(jsonPath("$.pixCopyPaste").value("copy"))
                .andExpect(jsonPath("$.pixProviderPaymentId").value("provider-123"));

        verify(createPaymentForSubscriptionService).create(subscriptionId);
    }

    @Test
    void create_whenSubscriptionNotFound_returns404_from_global_handler() throws Exception {
        Long subscriptionId = 999L;
        given(createPaymentForSubscriptionService.create(eq(subscriptionId)))
                .willThrow(new ResourceNotFoundException("Subscription", subscriptionId));

        mockMvc.perform(post("/api/subscriptions/{subscriptionId}/payments", subscriptionId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Subscription not found: 999"))
                .andExpect(jsonPath("$.path").value("/api/subscriptions/999/payments"));
    }
}
