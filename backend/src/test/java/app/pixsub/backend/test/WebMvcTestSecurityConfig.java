package app.pixsub.backend.test;

import app.pixsub.backend.auth.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class WebMvcTestSecurityConfig {

    @Bean
    JwtService testJwtService() {
        return new JwtService("test-secret-key-for-web-mvc-tests-32chars", 1L);
    }

    @Bean
    @Order(1)
    SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .build();
    }
}
