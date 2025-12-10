package app.pixsub.backend.student.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTest {
    @Test
    void newStudent_initializesActiveWithNullIdAndTimestamps() {
        Long trainerId = 100L;
        String name = "Aluno";
        String contact = "whatsapp:+5500000000";

        Student student = Student.newStudent(trainerId, name, contact);

        // id null before persistence
        assertThat(student.getId()).isNull();

        // fields mapped
        assertThat(student.getTrainerId()).isEqualTo(trainerId);
        assertThat(student.getName()).isEqualTo(name);
        assertThat(student.getContact()).isEqualTo(contact);

        // status ACTIVE by default
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);

        // timestamps set
        assertThat(student.getCreatedAt()).isNotNull();
        assertThat(student.getUpdatedAt()).isNotNull();
        assertThat(student.getCreatedAt())
                .isBeforeOrEqualTo(student.getUpdatedAt());
    }

    @Test
    void pause_returnsNewInstanceWithPausedStatus_preservingCoreFields() {
        Instant createdAt = Instant.now().minusSeconds(300);
        Instant updatedAt = Instant.now().minusSeconds(200);

        Student original = new Student(
                1L,
                100L,
                "Aluno",
                "contact",
                StudentStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        Student paused = original.pause();

        // original unchanged
        assertThat(original.getStatus()).isEqualTo(StudentStatus.ACTIVE);

        // new instance
        assertThat(paused).isNotSameAs(original);

        // core fields preserved
        assertThat(paused.getId()).isEqualTo(original.getId());
        assertThat(paused.getTrainerId()).isEqualTo(original.getTrainerId());
        assertThat(paused.getName()).isEqualTo(original.getName());
        assertThat(paused.getContact()).isEqualTo(original.getContact());
        assertThat(paused.getCreatedAt()).isEqualTo(original.getCreatedAt());

        // status updated + updatedAt refreshed
        assertThat(paused.getStatus()).isEqualTo(StudentStatus.PAUSED);
        assertThat(paused.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }

    @Test
    void activate_returnsNewInstanceWithActiveStatus() {
        Instant createdAt = Instant.now().minusSeconds(300);
        Instant updatedAt = Instant.now().minusSeconds(200);

        Student original = new Student(
                1L,
                100L,
                "Aluno",
                "contact",
                StudentStatus.PAUSED,
                createdAt,
                updatedAt
        );

        Student active = original.activate();

        assertThat(original.getStatus()).isEqualTo(StudentStatus.PAUSED);
        assertThat(active.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(active.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(active.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }

    @Test
    void deactivate_returnsNewInstanceWithInactiveStatus() {
        Instant createdAt = Instant.now().minusSeconds(300);
        Instant updatedAt = Instant.now().minusSeconds(200);

        Student original = new Student(
                1L,
                100L,
                "Aluno",
                "contact",
                StudentStatus.ACTIVE,
                createdAt,
                updatedAt
        );

        Student inactive = original.deactivate();

        assertThat(original.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(inactive.getStatus()).isEqualTo(StudentStatus.INACTIVE);
        assertThat(inactive.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(inactive.getUpdatedAt()).isAfterOrEqualTo(original.getUpdatedAt());
    }
}
