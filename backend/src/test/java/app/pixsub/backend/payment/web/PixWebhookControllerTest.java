package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.application.ProcessPixWebhookService;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import app.pixsub.backend.test.WebMvcTestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PixWebhookController.class)
@Import({GlobalExceptionHandler.class, WebMvcTestSecurityConfig.class})
class PixWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessPixWebhookService webhookService;

    @Test
    void efibankWebhook_extractsTxidAndCallsService() throws Exception {
        String payload = """
                {
                  "pix": [
                    {
                      "txid": "abc123",
                      "endToEndId": "E1234",
                      "valor": "100.00",
                      "horario": "2025-01-15T10:00:00Z"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/webhooks/pix/efibank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        verify(webhookService).process("abc123");
    }

    @Test
    void efibankWebhook_handlesMultiplePixEvents() throws Exception {
        String payload = """
                {
                  "pix": [
                    { "txid": "tx1", "valor": "50.00" },
                    { "txid": "tx2", "valor": "75.00" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/webhooks/pix/efibank")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        verify(webhookService).process("tx1");
        verify(webhookService).process("tx2");
    }

    @Test
    void genericWebhook_callsServiceWithProviderPaymentId() throws Exception {
        String payload = """
                { "providerPaymentId": "my-payment-id" }
                """;

        mockMvc.perform(post("/api/webhooks/pix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        verify(webhookService).process("my-payment-id");
    }
}
