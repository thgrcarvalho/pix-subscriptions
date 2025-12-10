package app.pixsub.backend.student.application;

import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListStudentsForTrainerServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ListStudentsForTrainerService service;

    @Test
    void list_returnsStudentsFromRepository() {
        Long trainerId = 7L;

        Student s1 = Student.newStudent(trainerId, "Alice", "whatsapp-1");
        Student s2 = Student.newStudent(trainerId, "Bob", "whatsapp-2");
        List<Student> students = List.of(s1, s2);

        // ✅ matches StudentRepositoryJpaAdapter#findByTrainerId
        given(studentRepository.findByTrainerId(trainerId)).willReturn(students);

        List<Student> result = service.list(trainerId);

        assertThat(result).isEqualTo(students);
        then(studentRepository).should().findByTrainerId(trainerId);
        then(studentRepository).shouldHaveNoMoreInteractions();
    }
}
