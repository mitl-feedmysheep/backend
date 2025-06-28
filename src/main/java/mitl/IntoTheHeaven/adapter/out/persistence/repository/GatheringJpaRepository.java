package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface GatheringJpaRepository extends JpaRepository<GatheringJpaEntity, UUID> {
    List<GatheringJpaEntity> findAllByGroupId(UUID groupId);

    @EntityGraph(attributePaths = {
            "gatheringMembers",
            "gatheringMembers.groupMember",
            "gatheringMembers.groupMember.member",
            "gatheringMembers.prayers"
    })
    Optional<GatheringJpaEntity> findWithDetailsById(UUID id);
} 