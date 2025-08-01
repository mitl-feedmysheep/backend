package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GatheringMemberJpaRepository extends JpaRepository<GatheringMemberJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"prayers"})
    List<GatheringMemberJpaEntity> findByGatheringIdIn(List<UUID> gatheringIds);
}