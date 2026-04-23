package app.pixsub.backend.student.application;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ListStudentsForTrainerServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ListStudentsForTrainerService service;

    @Test
    void list_returnsPageResultFromRepository() {
        Long trainerId = 7L;
        int page = 0;
        int size = 20;

        Student s1 = Student.newStudent(trainerId, "Alice", "whatsapp-1");
        Student s2 = Student.newStudent(trainerId, "Bob", "whatsapp-2");
        PageResult<Student> expected = new PageResult<>(List.of(s1, s2), 2, 1, page, size);

        given(studentRepository.findByTrainerId(trainerId, page, size)).willReturn(expected);

        PageResult<Student> result = service.list(trainerId, page, size);

        assertThat(result).isEqualTo(expected);
        then(studentRepository).should().findByTrainerId(trainerId, page, size);
        then(studentRepository).shouldHaveNoMoreInteractions();
    }
}
