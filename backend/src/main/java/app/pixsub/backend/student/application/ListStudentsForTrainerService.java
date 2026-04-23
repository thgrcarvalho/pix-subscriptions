package app.pixsub.backend.student.application;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class ListStudentsForTrainerService {
    private final StudentRepository studentRepository;

    public ListStudentsForTrainerService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public PageResult<Student> list(Long trainerId, int page, int size) {
        return studentRepository.findByTrainerId(trainerId, page, size);
    }
}
