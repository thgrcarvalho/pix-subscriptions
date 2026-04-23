package app.pixsub.backend.auth;

import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(TrainerRepository trainerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String rawPassword) {
        Trainer trainer = trainerRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, trainer.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(trainer.getId());
    }
}
