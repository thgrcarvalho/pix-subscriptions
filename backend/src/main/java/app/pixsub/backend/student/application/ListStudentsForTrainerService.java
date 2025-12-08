package app.pixsub.backend.student.application;

import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListStudentsForTrainerService {
    private final StudentRepository studentRepository;

    public ListStudentsForTrainerService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> list(Long trainerId) {
        return studentRepository.findByTrainerId(trainerId);
    }
}
