package app.pixsub.backend.student.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSpringDataRepository extends JpaRepository<StudentJpaEntity, Long> {
    List<StudentJpaEntity> findByTrainerId(Long trainerId);

    Page<StudentJpaEntity> findByTrainerId(Long trainerId, Pageable pageable);
}
