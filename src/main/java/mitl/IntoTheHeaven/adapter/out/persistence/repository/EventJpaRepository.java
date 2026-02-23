package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.EventJpaEntity;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, UUID> {

    List<EventJpaEntity> findAllByEntityIdAndEntityTypeAndDateBetweenOrderByDateAscStartTimeAsc(
            String entityId, EntityType entityType, LocalDate startDate, LocalDate endDate);
}
