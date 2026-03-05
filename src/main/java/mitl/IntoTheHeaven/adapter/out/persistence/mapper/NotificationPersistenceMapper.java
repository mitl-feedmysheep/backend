package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.NotificationJpaEntity;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Notification.builder()
                .id(NotificationId.from(entity.getId()))
                .receiverId(MemberId.from(entity.getReceiver().getId()))
                .senderId(entity.getSender() != null ? MemberId.from(entity.getSender().getId()) : null)
                .type(NotificationType.valueOf(entity.getType()))
                .description(entity.getDescription())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .targetUrl(entity.getTargetUrl())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationJpaEntity toEntity(Notification domain) {
        if (domain == null) {
            return null;
        }
        return NotificationJpaEntity.builder()
                .id(domain.getId().getValue())
                .receiver(MemberJpaEntity.builder().id(domain.getReceiverId().getValue()).build())
                .sender(domain.getSenderId() != null
                        ? MemberJpaEntity.builder().id(domain.getSenderId().getValue()).build()
                        : null)
                .type(domain.getType().getValue())
                .description(domain.getDescription())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .targetUrl(domain.getTargetUrl())
                .isRead(domain.isRead())
                .build();
    }
}
