package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, UUID> {
    Optional<MemberJpaEntity> findByEmail(String email);

    Optional<MemberJpaEntity> findByPhone(String phone);

    @EntityGraph(attributePaths = {"groupMembers", "groupMembers.group", "groupMembers.group.medias"})
    Optional<MemberJpaEntity> findWithGroupsById(UUID memberId);

    @EntityGraph(attributePaths = {"groupMembers", "groupMembers.group", "groupMembers.group.church", "groupMembers.group.medias"})
    Optional<MemberJpaEntity> findWithGroupsAndChurchesById(UUID memberId);

    List<MemberJpaEntity> findAllByGroupMembers_Group_Id(UUID groupId);

    @EntityGraph(attributePaths = {"groupMembers"})
    List<MemberJpaEntity> findAllWithGroupMembersByGroupMembers_Group_Id(UUID groupId);

    @EntityGraph(attributePaths = {"churchMembers", "churchMembers.church"})
    Optional<MemberJpaEntity> findWithChurchesById(UUID memberId);
} 