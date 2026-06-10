package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingPlanDayJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingPlanDayJpaRepository extends JpaRepository<ReadingPlanDayJpaEntity, UUID> {

    Optional<ReadingPlanDayJpaEntity> findByReadingPlanIdAndReadingDate(UUID readingPlanId, LocalDate readingDate);

    List<ReadingPlanDayJpaEntity> findByReadingPlanIdOrderByDayNumberAsc(UUID readingPlanId);
}
