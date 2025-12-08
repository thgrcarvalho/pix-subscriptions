package app.pixsub.backend.trainer.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Trainer {

    private final Long id;           // can be null before persistence
    private final String email;
    private final String passwordHash;
    private final String name;
    private final String pixKey;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Trainer(Long id,
                   String email,
                   String passwordHash,
                   String name,
                   String pixKey,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.pixKey = pixKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Trainer newTrainer(String email,
                                     String passwordHash,
                                     String name,
                                     String pixKey) {
        Instant now = Instant.now();
        return new Trainer(null, email, passwordHash, name, pixKey, now, now);
    }

    public Trainer withUpdatedPixKey(String newPixKey) {
        return new Trainer(id, email, passwordHash, name, newPixKey, createdAt, Instant.now());
    }
}
