package app.pixsub.backend.trainer.application;

import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterTrainerService {
    private final TrainerRepository trainerRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public RegisterTrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public Trainer register(String email, String rawPassword, String name, String pixKey) {
        if (trainerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        String hash = encoder.encode(rawPassword);
        Trainer trainer = Trainer.newTrainer(email, hash, name, pixKey);
        return trainerRepository.save(trainer);
    }
}
