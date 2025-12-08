package app.pixsub.backend.trainer.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerSpringDataRepository extends JpaRepository<TrainerJpaEntity, Long> {
    Optional<TrainerJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
