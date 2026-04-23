package app.pixsub.backend.student.infrastructure;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepositoryJpaAdapter implements StudentRepository {
    private final StudentSpringDataRepository springData;

    public StudentRepositoryJpaAdapter(StudentSpringDataRepository springData) {
        this.springData = springData;
    }

    @Override
    public Student save(Student student) {
        StudentJpaEntity entity = StudentJpaEntity.fromDomain(student);
        StudentJpaEntity saved = springData.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return springData.findById(id).map(StudentJpaEntity::toDomain);
    }

    @Override
    public List<Student> findByTrainerId(Long trainerId) {
        return springData.findByTrainerId(trainerId)
                .stream()
                .map(StudentJpaEntity::toDomain)
                .toList();
    }

    @Override
    public PageResult<Student> findByTrainerId(Long trainerId, int page, int size) {
        Page<StudentJpaEntity> p = springData.findByTrainerId(trainerId, PageRequest.of(page, size));
        return new PageResult<>(p.getContent().stream().map(StudentJpaEntity::toDomain).toList(),
                p.getTotalElements(), p.getTotalPages(), page, size);
    }
}
