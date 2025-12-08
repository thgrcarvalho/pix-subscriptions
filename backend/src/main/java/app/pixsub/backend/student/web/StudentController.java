package app.pixsub.backend.student.web;

import app.pixsub.backend.student.application.CreateStudentService;
import app.pixsub.backend.student.application.ListStudentsForTrainerService;
import app.pixsub.backend.student.domain.Student;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final CreateStudentService createStudentService;
    private final ListStudentsForTrainerService listStudentsForTrainerService;

    public StudentController(CreateStudentService createStudentService,
                             ListStudentsForTrainerService listStudentsForTrainerService) {
        this.createStudentService = createStudentService;
        this.listStudentsForTrainerService = listStudentsForTrainerService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentCreateRequest request) {
        Student student = createStudentService.create(
                request.getTrainerId(),
                request.getName(),
                request.getContact()
        );

        StudentResponse response = toResponse(student);
        return ResponseEntity
                .created(URI.create("/api/students/" + student.getId()))
                .body(response);
    }

    @GetMapping
    public List<StudentResponse> listByTrainer(@RequestParam("trainerId") Long trainerId) {
        return listStudentsForTrainerService.list(trainerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getTrainerId(),
                student.getName(),
                student.getContact(),
                student.getStatus(),
                student.getCreatedAt()
        );
    }
}
