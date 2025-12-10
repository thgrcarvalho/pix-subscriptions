package app.pixsub.backend.trainer.infrastructure;

import app.pixsub.backend.test.AbstractPostgresContainerTest;
import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrainerRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void saveAndLoadTrainer_worksWithRealDatabaseSchema() {
        Trainer newTrainer = Trainer.newTrainer(
                "repo-test@example.com",
                "HASH",
                "Repo Test",
                "pix-key-repo"
        );

        Trainer saved = trainerRepository.save(newTrainer);

        assertThat(saved.getId()).isNotNull();

        Trainer loaded = trainerRepository.findById(saved.getId())
                .orElseThrow();

        assertThat(loaded.getEmail()).isEqualTo("repo-test@example.com");
        assertThat(loaded.getName()).isEqualTo("Repo Test");
        assertThat(loaded.getPixKey()).isEqualTo("pix-key-repo");
    }
}
