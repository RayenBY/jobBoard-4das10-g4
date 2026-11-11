package tn.university.ms3.repository;
import tn.university.ms3.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<Course,Long> {}
