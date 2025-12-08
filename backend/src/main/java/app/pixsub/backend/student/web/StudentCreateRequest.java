package app.pixsub.backend.student.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StudentCreateRequest {
    @NotNull
    private Long trainerId;

    @NotBlank
    private String name;

    private String contact;
}
