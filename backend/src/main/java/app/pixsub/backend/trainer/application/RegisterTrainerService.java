package app.pixsub.backend.trainer.application;

import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTrainerService {

    private final TrainerRepository trainerRepository;
    private final PasswordEncoder encoder;   // no new BCryptPasswordEncoder() here

    @Transactional
    public Trainer register(String email, String rawPassword, String name, String pixKey) {
        if (trainerRepository.existsByEmail(email)) {
            throw new DomainValidationException("Email already in use");
        }

        String hash = encoder.encode(rawPassword);
        Trainer trainer = Trainer.newTrainer(email, hash, name, pixKey);
        return trainerRepository.save(trainer);
    }
}
