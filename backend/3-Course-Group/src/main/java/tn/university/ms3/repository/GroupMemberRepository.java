package tn.university.ms3.repository;
import tn.university.ms3.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> { List<GroupMember> findByStudyGroupId(Long groupId); }
