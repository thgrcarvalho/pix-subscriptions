package app.pixsub.backend.trainer.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerTest {
    @Test
    void newTrainer_initializesWithNullIdAndTimestamps() {
        String email = "thiago@example.com";
        String hash = "HASHED";
        String name = "Thiago";
        String pixKey = "pix-key-123";

        Trainer trainer = Trainer.newTrainer(email, hash, name, pixKey);

        // id is null before persistence
        assertThat(trainer.getId()).isNull();

        // fields mapped correctly
        assertThat(trainer.getEmail()).isEqualTo(email);
        assertThat(trainer.getPasswordHash()).isEqualTo(hash);
        assertThat(trainer.getName()).isEqualTo(name);
        assertThat(trainer.getPixKey()).isEqualTo(pixKey);

        // timestamps initialized
        assertThat(trainer.getCreatedAt()).isNotNull();
        assertThat(trainer.getUpdatedAt()).isNotNull();
        assertThat(trainer.getCreatedAt())
                .isBeforeOrEqualTo(trainer.getUpdatedAt());
    }

    @Test
    void withUpdatedPixKey_returnsNewInstanceWithUpdatedPixKeyAndUpdatedTimestamp() {
        Instant createdAt = Instant.now().minusSeconds(120);
        Instant updatedAt = Instant.now().minusSeconds(60);

        Trainer original = new Trainer(
                1L,
                "thiago@example.com",
                "HASHED",
                "Thiago",
                "old-pix-key",
                createdAt,
                updatedAt
        );

        Trainer updated = original.withUpdatedPixKey("new-pix-key");

        // immutability: original unchanged
        assertThat(original.getPixKey()).isEqualTo("old-pix-key");

        // new instance
        assertThat(updated).isNotSameAs(original);

        // identity & core fields preserved
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getEmail()).isEqualTo(original.getEmail());
        assertThat(updated.getPasswordHash()).isEqualTo(original.getPasswordHash());
        assertThat(updated.getName()).isEqualTo(original.getName());
        assertThat(updated.getCreatedAt()).isEqualTo(original.getCreatedAt());

        // pixKey changed
        assertThat(updated.getPixKey()).isEqualTo("new-pix-key");

        // updatedAt refreshed
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }
}
