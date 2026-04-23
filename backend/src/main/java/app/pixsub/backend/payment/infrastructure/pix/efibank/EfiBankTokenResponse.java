package app.pixsub.backend.payment.infrastructure.pix.efibank;

import com.fasterxml.jackson.annotation.JsonProperty;

record EfiBankTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expiresIn
) {}
