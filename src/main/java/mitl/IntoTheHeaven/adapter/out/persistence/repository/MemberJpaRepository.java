package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, UUID> {
    Optional<MemberJpaEntity> findByEmail(String email);

    @EntityGraph(attributePaths = {"groupMembers", "groupMembers.group"})
    Optional<MemberJpaEntity> findWithGroupsById(UUID memberId);
} 