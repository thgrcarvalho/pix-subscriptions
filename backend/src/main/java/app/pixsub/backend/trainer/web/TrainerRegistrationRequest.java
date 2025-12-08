package app.pixsub.backend.trainer.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TrainerRegistrationRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String pixKey;
}
