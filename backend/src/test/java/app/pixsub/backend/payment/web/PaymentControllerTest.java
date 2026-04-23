package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.application.ListPaymentsForSubscriptionService;
import app.pixsub.backend.payment.application.MarkPaymentPaidService;
import app.pixsub.backend.payment.domain.Payment;
import app.pixsub.backend.payment.domain.PaymentStatus;
import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import app.pixsub.backend.test.WebMvcTestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@Import({GlobalExceptionHandler.class, WebMvcTestSecurityConfig.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListPaymentsForSubscriptionService listPaymentsForSubscriptionService;

    @MockitoBean
    private MarkPaymentPaidService markPaymentPaidService;

    @Test
    void markPaid_whenPaymentExists_returns200WithPaymentResponse() throws Exception {
        Payment payment = new Payment(
                10L,
                5L,
                100_00L,
                LocalDate.of(2025, 1, 15),
                Instant.now(),
                PaymentStatus.PAID,
                null,
                "QR",
                "COPY-PASTE",
                "PROVIDER-ID",
                Instant.now().minusSeconds(120),
                Instant.now()
        );

        given(markPaymentPaidService.markPaid(anyLong()))
                .willReturn(payment);

        mockMvc.perform(post("/api/payments/10/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.subscriptionId").value(5L))
                .andExpect(jsonPath("$.amountInCents").value(100_00L))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.dueDate").value("2025-01-15"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void markPaid_whenPaymentNotFound_returns404WithErrorBody() throws Exception {
        given(markPaymentPaidService.markPaid(anyLong()))
                .willThrow(new ResourceNotFoundException("Payment", 99L));

        mockMvc.perform(post("/api/payments/99/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("Payment not found")))
                .andExpect(jsonPath("$.path").value("/api/payments/99/pay"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void listBySubscription_returnsListOfPayments() throws Exception {
        Payment p1 = new Payment(
                10L,
                5L,
                100_00L,
                LocalDate.of(2025, 1, 15),
                null,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                null,
                Instant.now().minusSeconds(300),
                Instant.now().minusSeconds(200)
        );
        Payment p2 = new Payment(
                11L,
                5L,
                100_00L,
                LocalDate.of(2025, 2, 15),
                null,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                null,
                Instant.now().minusSeconds(200),
                Instant.now().minusSeconds(100)
        );

        given(listPaymentsForSubscriptionService.listBySubscription(5L, 0, 20))
                .willReturn(new PageResult<>(List.of(p1, p2), 2, 1, 0, 20));

        mockMvc.perform(get("/api/payments")
                        .param("subscriptionId", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(10L))
                .andExpect(jsonPath("$.content[1].id").value(11L))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
