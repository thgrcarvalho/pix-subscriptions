package app.pixsub.backend.student.domain;

import app.pixsub.backend.shared.PageResult;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findById(Long id);

    List<Student> findByTrainerId(Long trainerId);

    PageResult<Student> findByTrainerId(Long trainerId, int page, int size);
}
