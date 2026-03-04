package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    List<NotificationJpaEntity> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

    long countByReceiverIdAndIsReadFalse(UUID receiverId);

    boolean existsByReceiverIdAndTypeAndEntityTypeAndEntityIdAndIsReadFalse(
            UUID receiverId, String type, String entityType, String entityId);
}
