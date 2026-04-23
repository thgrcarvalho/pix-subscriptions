package app.pixsub.backend.payment.application;

public interface PixGateway {
    String provider();

    PixCharge createCharge(long amountInCents, String description);
}
