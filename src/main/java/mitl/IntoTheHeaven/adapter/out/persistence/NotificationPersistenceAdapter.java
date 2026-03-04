package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.NotificationJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.NotificationJpaRepository;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationPort {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationPersistenceMapper notificationPersistenceMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = notificationPersistenceMapper.toEntity(notification);
        NotificationJpaEntity saved = notificationJpaRepository.save(entity);
        return notificationPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Notification> findByReceiverId(UUID receiverId) {
        return notificationJpaRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(notificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByReceiverId(UUID receiverId) {
        return notificationJpaRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    @Override
    public Optional<Notification> findById(UUID notificationId) {
        return notificationJpaRepository.findById(notificationId)
                .map(notificationPersistenceMapper::toDomain);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        notificationJpaRepository.findById(notificationId).ifPresent(entity -> {
            entity.markAsRead();
            notificationJpaRepository.save(entity);
        });
    }

    @Override
    public boolean existsUnreadByReceiverAndTypeAndEntity(UUID receiverId, String type, String entityType, String entityId) {
        return notificationJpaRepository.existsByReceiverIdAndTypeAndEntityTypeAndEntityIdAndIsReadFalse(
                receiverId, type, entityType, entityId);
    }
}
