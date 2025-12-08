package app.pixsub.backend.trainer.infrastructure;

import app.pixsub.backend.trainer.domain.Trainer;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "trainer", uniqueConstraints = {
        @UniqueConstraint(name = "uq_trainer_email", columnNames = "email")
})
public class TrainerJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String name;

    @Column(name = "pix_key")
    private String pixKey;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected TrainerJpaEntity() {
    }

    public static TrainerJpaEntity fromDomain(Trainer trainer) {
        TrainerJpaEntity e = new TrainerJpaEntity();
        e.id = trainer.getId();
        e.email = trainer.getEmail();
        e.passwordHash = trainer.getPasswordHash();
        e.name = trainer.getName();
        e.pixKey = trainer.getPixKey();
        e.createdAt = trainer.getCreatedAt();
        e.updatedAt = trainer.getUpdatedAt();
        return e;
    }

    public Trainer toDomain() {
        return new Trainer(
                id,
                email,
                passwordHash,
                name,
                pixKey,
                createdAt,
                updatedAt
        );
    }
}
