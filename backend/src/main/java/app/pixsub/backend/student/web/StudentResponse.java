package app.pixsub.backend.student.web;

import app.pixsub.backend.student.domain.StudentStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class StudentResponse {
    private Long id;
    private Long trainerId;
    private String name;
    private String contact;
    private StudentStatus status;
    private Instant createdAt;

    public StudentResponse(Long id,
                           Long trainerId,
                           String name,
                           String contact,
                           StudentStatus status,
                           Instant createdAt) {
        this.id = id;
        this.trainerId = trainerId;
        this.name = name;
        this.contact = contact;
        this.status = status;
        this.createdAt = createdAt;
    }
}
