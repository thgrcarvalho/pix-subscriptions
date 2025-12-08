package app.pixsub.backend.trainer.infrastructure;

import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainerRepositoryJpaAdapter implements TrainerRepository {
    private final TrainerSpringDataRepository springData;

    public TrainerRepositoryJpaAdapter(TrainerSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public Trainer save(Trainer trainer) {
        TrainerJpaEntity entity = TrainerJpaEntity.fromDomain(trainer);
        TrainerJpaEntity saved = springData.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return springData.findById(id).map(TrainerJpaEntity::toDomain);
    }

    @Override
    public Optional<Trainer> findByEmail(String email) {
        return springData.findByEmail(email).map(TrainerJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springData.existsByEmail(email);
    }
}
