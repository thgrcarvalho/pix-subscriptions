package app.pixsub.backend.trainer.web;

import lombok.Getter;

import java.time.Instant;

@Getter
public class TrainerResponse {
    private Long id;
    private String email;
    private String name;
    private String pixKey;
    private Instant createdAt;

    public TrainerResponse(Long id, String email, String name, String pixKey, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.pixKey = pixKey;
        this.createdAt = createdAt;
    }
}
