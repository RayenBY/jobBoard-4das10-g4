package tn.university.ms3.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="courses")
public class Course {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(length=2000) private String description;
 private String subject; private String level; private Long tutorId; private LocalDateTime createdAt;
 @PrePersist void onCreate(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
 public Course(){}
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getSubject(){return subject;} public void setSubject(String v){subject=v;}
 public String getLevel(){return level;} public void setLevel(String v){level=v;}
 public Long getTutorId(){return tutorId;} public void setTutorId(Long v){tutorId=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
