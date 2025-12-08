package app.pixsub.backend.student.domain;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Student {
    private final Long id;
    private final Long trainerId;
    private final String name;
    private final String contact;    // WhatsApp / email / etc.
    private final StudentStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Student(Long id,
                   Long trainerId,
                   String name,
                   String contact,
                   StudentStatus status,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.trainerId = trainerId;
        this.name = name;
        this.contact = contact;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Student newStudent(Long trainerId, String name, String contact) {
        Instant now = Instant.now();
        return new Student(null, trainerId, name, contact, StudentStatus.ACTIVE, now, now);
    }

    public Student pause() {
        return new Student(id, trainerId, name, contact, StudentStatus.PAUSED, createdAt, Instant.now());
    }

    public Student activate() {
        return new Student(id, trainerId, name, contact, StudentStatus.ACTIVE, createdAt, Instant.now());
    }

    public Student deactivate() {
        return new Student(id, trainerId, name, contact, StudentStatus.INACTIVE, createdAt, Instant.now());
    }
}
