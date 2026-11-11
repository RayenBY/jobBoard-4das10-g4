package tn.university.ms3.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="enrollments")
public class Enrollment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 // studentId est gere par MS2 : pas de cle etrangere entre bases.
 private Long studentId; private Double progress;
 @Enumerated(EnumType.STRING) private EnrollmentStatus status;
 private LocalDateTime enrolledAt; private LocalDateTime completedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="course_id",nullable=false) private Course course;
 @PrePersist void onCreate(){if(enrolledAt==null)enrolledAt=LocalDateTime.now();if(status==null)status=EnrollmentStatus.ACTIVE;if(progress==null)progress=0.0;}
 public Enrollment(){}
 public Long getId(){return id;} public Long getStudentId(){return studentId;} public void setStudentId(Long v){studentId=v;}
 public Double getProgress(){return progress;} public void setProgress(Double v){progress=v;}
 public EnrollmentStatus getStatus(){return status;} public void setStatus(EnrollmentStatus v){status=v;}
 public LocalDateTime getEnrolledAt(){return enrolledAt;} public LocalDateTime getCompletedAt(){return completedAt;}
 public void setCompletedAt(LocalDateTime v){completedAt=v;}
 public Course getCourse(){return course;} public void setCourse(Course v){course=v;}
}
