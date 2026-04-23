package app.pixsub.backend.payment.infrastructure.pix.efibank;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pixsub.pix.efibank")
public class EfiBankProperties {

    /** EfiBank OAuth2 client ID. */
    private String clientId;

    /** EfiBank OAuth2 client secret. */
    private String clientSecret;

    /**
     * EfiBank API base URL.
     * Homologation: https://pix-h.api.efipay.com.br
     * Production:   https://pix.api.efipay.com.br
     */
    private String baseUrl = "https://pix-h.api.efipay.com.br";

    /** Your registered Pix key (chave Pix) to receive charges. */
    private String pixKey;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getPixKey() { return pixKey; }
    public void setPixKey(String pixKey) { this.pixKey = pixKey; }
}
