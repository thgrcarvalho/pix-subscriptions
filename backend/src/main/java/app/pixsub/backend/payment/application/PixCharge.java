package app.pixsub.backend.payment.application;

public record PixCharge(
        String qrCode,
        String copyPaste,
        String providerPaymentId
) {}
