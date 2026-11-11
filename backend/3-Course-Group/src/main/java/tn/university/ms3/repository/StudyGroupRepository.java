package tn.university.ms3.repository;
import tn.university.ms3.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudyGroupRepository extends JpaRepository<StudyGroup,Long> { List<StudyGroup> findByCourseId(Long courseId); }
