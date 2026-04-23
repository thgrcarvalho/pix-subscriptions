package app.pixsub.backend.student.web;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.student.application.CreateStudentService;
import app.pixsub.backend.student.application.ListStudentsForTrainerService;
import app.pixsub.backend.student.domain.Student;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PageResult<StudentResponse> listByTrainer(
            @RequestParam("trainerId") Long trainerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Student> result = listStudentsForTrainerService.list(trainerId, page, size);
        return new PageResult<>(result.content().stream().map(this::toResponse).toList(),
                result.totalElements(), result.totalPages(), result.page(), result.size());
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
