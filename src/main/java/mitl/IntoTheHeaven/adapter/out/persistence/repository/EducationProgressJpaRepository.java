package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationProgressJpaRepository extends JpaRepository<EducationProgressJpaEntity, UUID> {

    List<EducationProgressJpaEntity> findByGroupMemberIdIn(List<UUID> groupMemberIds);

    List<EducationProgressJpaEntity> findByGatheringId(UUID gatheringId);
}
