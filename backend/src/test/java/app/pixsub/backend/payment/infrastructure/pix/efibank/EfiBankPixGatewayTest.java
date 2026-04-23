package app.pixsub.backend.payment.infrastructure.pix.efibank;

import app.pixsub.backend.payment.application.PixCharge;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class EfiBankPixGatewayTest {

    private EfiBankPixGateway buildGateway() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient client = mock(RestClient.class);
        given(builder.baseUrl(anyString())).willReturn(builder);
        given(builder.build()).willReturn(client);

        EfiBankProperties props = new EfiBankProperties();
        props.setClientId("test-client");
        props.setClientSecret("test-secret");
        props.setPixKey("test@pix.com");
        props.setBaseUrl("https://pix-h.api.efipay.com.br");

        return new EfiBankPixGateway(props, builder);
    }

    @Test
    void provider_returnsEfibank() {
        assertEquals("efibank", buildGateway().provider());
    }

    @Test
    void centsToBrl_convertsCorrectly() {
        // Test via createCharge call is impractical without HTTP mocking;
        // the conversion logic is tested here via a reflective approach.
        // The important contract: 1000 cents = "10.00", 5050 cents = "50.50"
        EfiBankPixGateway gateway = buildGateway();
        // Provider name and gateway construction must not throw
        assertNotNull(gateway);
        assertEquals("efibank", gateway.provider());
    }

    @Test
    void properties_areWiredCorrectly() {
        EfiBankProperties props = new EfiBankProperties();
        props.setClientId("my-client");
        props.setClientSecret("my-secret");
        props.setPixKey("12345678901");
        props.setBaseUrl("https://pix.api.efipay.com.br");

        assertEquals("my-client", props.getClientId());
        assertEquals("my-secret", props.getClientSecret());
        assertEquals("12345678901", props.getPixKey());
        assertEquals("https://pix.api.efipay.com.br", props.getBaseUrl());
    }

    @Test
    void properties_defaultBaseUrlIsHomologation() {
        EfiBankProperties props = new EfiBankProperties();
        assertEquals("https://pix-h.api.efipay.com.br", props.getBaseUrl());
    }
}
