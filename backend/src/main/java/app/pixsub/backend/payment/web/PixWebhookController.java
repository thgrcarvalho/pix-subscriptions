package app.pixsub.backend.payment.web;

import app.pixsub.backend.payment.application.ProcessPixWebhookService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks/pix")
public class PixWebhookController {

    private final ProcessPixWebhookService webhookService;

    public PixWebhookController(ProcessPixWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Receives EfiBank / Sicredi Pix webhook notifications.
     *
     * Payload format:
     * <pre>
     * {
     *   "pix": [
     *     { "txid": "...", "endToEndId": "E...", "valor": "100.00", "horario": "..." }
     *   ]
     * }
     * </pre>
     * The {@code txid} is what we store as {@code pixProviderPaymentId}.
     */
    @PostMapping("/efibank")
    public ResponseEntity<Void> efibankWebhook(@RequestBody EfibankWebhookPayload payload) {
        if (payload.pix() != null) {
            for (EfibankPixEvent event : payload.pix()) {
                if (event.txid() != null) {
                    webhookService.process(event.txid());
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Generic webhook endpoint — useful for testing or custom integrations.
     * Accepts {@code { "providerPaymentId": "..." }}.
     */
    @PostMapping
    public ResponseEntity<Void> genericWebhook(@RequestBody GenericWebhookPayload payload) {
        webhookService.process(payload.providerPaymentId());
        return ResponseEntity.ok().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EfibankWebhookPayload(List<EfibankPixEvent> pix) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EfibankPixEvent(String txid, String endToEndId, String valor, String horario) {}

    public record GenericWebhookPayload(String providerPaymentId) {}
}
