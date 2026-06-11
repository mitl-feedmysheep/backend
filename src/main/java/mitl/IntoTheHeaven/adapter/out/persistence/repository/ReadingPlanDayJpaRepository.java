package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingPlanDayJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingPlanDayJpaRepository extends JpaRepository<ReadingPlanDayJpaEntity, UUID> {

    Optional<ReadingPlanDayJpaEntity> findByReadingPlanIdAndDayNumber(UUID readingPlanId, int dayNumber);

    List<ReadingPlanDayJpaEntity> findByReadingPlanIdOrderByDayNumberAsc(UUID readingPlanId);

    long countByReadingPlanId(UUID readingPlanId);
}
