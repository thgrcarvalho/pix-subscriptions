package app.pixsub.backend.payment.infrastructure.pix;

import app.pixsub.backend.payment.application.PixCharge;
import app.pixsub.backend.payment.application.PixGateway;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Primary // so the app runs without choosing a real provider yet
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
