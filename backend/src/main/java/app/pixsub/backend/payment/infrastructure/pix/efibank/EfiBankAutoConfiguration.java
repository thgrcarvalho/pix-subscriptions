package app.pixsub.backend.payment.infrastructure.pix.efibank;

import app.pixsub.backend.payment.application.PixGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Activates the EfiBank {@link PixGateway} when {@code pixsub.pix.provider=efibank}.
 *
 * <p>Required properties:</p>
 * <pre>
 * pixsub.pix.provider=efibank
 * pixsub.pix.efibank.client-id=YOUR_CLIENT_ID
 * pixsub.pix.efibank.client-secret=YOUR_CLIENT_SECRET
 * pixsub.pix.efibank.pix-key=YOUR_PIX_KEY
 * # Optional — defaults to homologation:
 * pixsub.pix.efibank.base-url=https://pix-h.api.efipay.com.br
 * </pre>
 */
@Configuration
@ConditionalOnProperty(name = "pixsub.pix.provider", havingValue = "efibank")
@EnableConfigurationProperties(EfiBankProperties.class)
public class EfiBankAutoConfiguration {

    @Bean
    PixGateway efiBankPixGateway(EfiBankProperties properties, RestClient.Builder restClientBuilder) {
        return new EfiBankPixGateway(properties, restClientBuilder);
    }
}
