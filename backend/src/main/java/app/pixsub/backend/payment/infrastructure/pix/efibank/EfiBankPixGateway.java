package app.pixsub.backend.payment.infrastructure.pix.efibank;

import app.pixsub.backend.payment.application.PixCharge;
import app.pixsub.backend.payment.application.PixGateway;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * {@link PixGateway} backed by the EfiBank (formerly Gerencianet) Pix API.
 *
 * <p>Activate by setting {@code pixsub.pix.provider=efibank} together with
 * the required {@code pixsub.pix.efibank.*} properties. The homologation
 * environment is used by default; flip {@code pixsub.pix.efibank.base-url}
 * to production when ready.</p>
 *
 * <p>Authentication uses OAuth2 client credentials. Each charge request
 * obtains a fresh token. For production, tokens should be cached and
 * refreshed only when close to expiry.</p>
 */
public class EfiBankPixGateway implements PixGateway {

    private static final String PROVIDER = "efibank";

    private final EfiBankProperties properties;
    private final RestClient restClient;

    public EfiBankPixGateway(EfiBankProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
    }

    @Override
    public String provider() {
        return PROVIDER;
    }

    @Override
    public PixCharge createCharge(long amountInCents, String description) {
        String token = authenticate();
        String txid = UUID.randomUUID().toString().replace("-", "").substring(0, 26);

        CreateCobRequest request = new CreateCobRequest(
                new CreateCobRequest.Calendar(3600),
                new CreateCobRequest.Valor(centsToBrl(amountInCents)),
                properties.getPixKey(),
                description
        );

        CobResponse cob = restClient.put()
                .uri("/v2/cob/{txid}", txid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CobResponse.class);

        String qrCode = fetchQrCode(token, cob.loc().id());

        return new PixCharge(qrCode, cob.pixCopiaECola(), txid);
    }

    private String authenticate() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        EfiBankTokenResponse token = restClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(h -> h.setBasicAuth(properties.getClientId(), properties.getClientSecret()))
                .body(form)
                .retrieve()
                .body(EfiBankTokenResponse.class);

        return token.accessToken();
    }

    private String fetchQrCode(String token, long locId) {
        QrCodeResponse qr = restClient.get()
                .uri("/v2/loc/{id}/qrcode", locId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(QrCodeResponse.class);
        return qr.imagemQrcode();
    }

    private static String centsToBrl(long cents) {
        return BigDecimal.valueOf(cents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY)
                .toPlainString();
    }

    // ── Request / response DTOs ──────────────────────────────────────────────

    record CreateCobRequest(
            Calendar calendario,
            Valor valor,
            @JsonProperty("chave") String pixKey,
            @JsonProperty("solicitacaoPagador") String description
    ) {
        record Calendar(@JsonProperty("expiracao") int expiracao) {}
        record Valor(@JsonProperty("original") String original) {}
    }

    record CobResponse(
            String txid,
            @JsonProperty("pixCopiaECola") String pixCopiaECola,
            Loc loc
    ) {
        record Loc(long id) {}
    }

    record QrCodeResponse(
            @JsonProperty("imagemQrcode") String imagemQrcode,
            @JsonProperty("qrcode") String qrcode
    ) {}
}
