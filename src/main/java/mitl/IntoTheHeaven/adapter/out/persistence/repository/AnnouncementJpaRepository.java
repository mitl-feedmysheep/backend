package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.AnnouncementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnnouncementJpaRepository extends JpaRepository<AnnouncementJpaEntity, UUID> {

    List<AnnouncementJpaEntity> findTop2ByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);

    List<AnnouncementJpaEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);

    List<AnnouncementJpaEntity> findBySendAtBeforeAndIsSentFalse(LocalDateTime now);

    @Modifying
    @Query(value = "UPDATE announcement SET deleted_at = NOW() WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
    void softDeleteById(@Param("id") UUID id);
}
