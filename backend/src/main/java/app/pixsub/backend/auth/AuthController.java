package app.pixsub.backend.auth;

import io.github.thgrcarvalho.ratelimit.RateLimit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @RateLimit(requests = 10, window = "1m", keyStrategy = RateLimit.KeyStrategy.IP_AND_PATH)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

    public record TokenResponse(String token) {}
}
