package tn.university.ms3.repository;
import tn.university.ms3.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> { List<Enrollment> findByCourseId(Long courseId); }
