package app.pixsub.backend.student.infrastructure;

import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "student")
public class StudentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_info")
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected StudentJpaEntity() {
    }

    public static StudentJpaEntity fromDomain(Student student) {
        StudentJpaEntity e = new StudentJpaEntity();
        e.id = student.getId();
        e.trainerId = student.getTrainerId();
        e.name = student.getName();
        e.contact = student.getContact();
        e.status = student.getStatus();
        e.createdAt = student.getCreatedAt();
        e.updatedAt = student.getUpdatedAt();
        return e;
    }

    public Student toDomain() {
        return new Student(
                id,
                trainerId,
                name,
                contact,
                status,
                createdAt,
                updatedAt
        );
    }
}
