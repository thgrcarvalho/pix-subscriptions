package app.pixsub.backend.trainer.domain;

import java.util.Optional;

public interface TrainerRepository {

    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long id);

    Optional<Trainer> findByEmail(String email);

    boolean existsByEmail(String email);
}
