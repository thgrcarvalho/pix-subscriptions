package app.pixsub.backend.student.application;

import app.pixsub.backend.shared.ResourceNotFoundException;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateStudentService {
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;

    public CreateStudentService(StudentRepository studentRepository,
                                TrainerRepository trainerRepository) {
        this.studentRepository = studentRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public Student create(Long trainerId, String name, String contact) {
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));

        Student student = Student.newStudent(trainerId, name, contact);
        return studentRepository.save(student);
    }
}
