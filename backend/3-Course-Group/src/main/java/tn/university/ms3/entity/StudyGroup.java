package tn.university.ms3.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="study_groups")
public class StudyGroup {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String name; private String description; private Integer capacity; private LocalDateTime createdAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="course_id",nullable=false) private Course course;
 @PrePersist void onCreate(){if(createdAt==null)createdAt=LocalDateTime.now();}
 public StudyGroup(){}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public Integer getCapacity(){return capacity;} public void setCapacity(Integer v){capacity=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
 public Course getCourse(){return course;} public void setCourse(Course v){course=v;}
}
