package app.pixsub.backend.trainer.web;

import app.pixsub.backend.trainer.application.RegisterTrainerService;
import app.pixsub.backend.trainer.domain.Trainer;
import io.github.thgrcarvalho.ratelimit.RateLimit;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final RegisterTrainerService registerTrainerService;

    public TrainerController(RegisterTrainerService registerTrainerService) {
        this.registerTrainerService = registerTrainerService;
    }

    @PostMapping("/register")
    @RateLimit(requests = 5, window = "1m", keyStrategy = RateLimit.KeyStrategy.IP_AND_PATH)
    public ResponseEntity<TrainerResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        Trainer trainer = registerTrainerService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getPixKey()
        );

        TrainerResponse response = new TrainerResponse(
                trainer.getId(),
                trainer.getEmail(),
                trainer.getName(),
                trainer.getPixKey(),
                trainer.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}
