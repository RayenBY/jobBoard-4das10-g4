package tn.university.ms3.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="group_members")
public class GroupMember {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 // userId est gere par MS2 : pas de cle etrangere entre bases de microservices.
 private Long userId;
 @Enumerated(EnumType.STRING) private MemberRole role;
 private LocalDateTime joinedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="study_group_id",nullable=false) private StudyGroup studyGroup;
 @PrePersist void onCreate(){if(joinedAt==null)joinedAt=LocalDateTime.now();}
 public GroupMember(){}
 public Long getId(){return id;} public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
 public MemberRole getRole(){return role;} public void setRole(MemberRole v){role=v;}
 public LocalDateTime getJoinedAt(){return joinedAt;}
 public StudyGroup getStudyGroup(){return studyGroup;} public void setStudyGroup(StudyGroup v){studyGroup=v;}
}
