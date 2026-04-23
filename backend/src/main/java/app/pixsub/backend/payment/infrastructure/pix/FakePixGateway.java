package app.pixsub.backend.payment.infrastructure.pix;

import app.pixsub.backend.payment.application.PixCharge;
import app.pixsub.backend.payment.application.PixGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "pixsub.pix.provider", havingValue = "fake", matchIfMissing = true)
public class FakePixGateway implements PixGateway {
    @Override
    public String provider() {
        return "fake";
    }

    @Override
    public PixCharge createCharge(long amountInCents, String description) {
        String id = UUID.randomUUID().toString();
        return new PixCharge(
                "FAKE_QR_CODE_" + amountInCents,
                "FAKE_COPY_PASTE_" + id,
                "fake-" + id
        );
    }
}
