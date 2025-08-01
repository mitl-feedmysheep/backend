package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMemberJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"member"})
    List<GroupMemberJpaEntity> findByGroupIdOrderByRoleAscMemberBirthdayAsc(UUID groupId);
}