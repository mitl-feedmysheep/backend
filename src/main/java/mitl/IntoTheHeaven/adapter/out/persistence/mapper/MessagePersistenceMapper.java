package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MessageJpaEntity;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import mitl.IntoTheHeaven.domain.model.MessageId;
import org.springframework.stereotype.Component;

@Component
public class MessagePersistenceMapper {

    public Message toDomain(MessageJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Message.builder()
                .id(MessageId.from(entity.getId()))
                .senderId(MemberId.from(entity.getSender().getId()))
                .senderName(entity.getSender().getName())
                .receiverId(MemberId.from(entity.getReceiver().getId()))
                .receiverName(entity.getReceiver().getName())
                .content(entity.getMessage())
                .type(entity.getType())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MessageJpaEntity toEntity(Message domain) {
        if (domain == null) {
            return null;
        }
        return MessageJpaEntity.builder()
                .id(domain.getId().getValue())
                .message(domain.getContent())
                .type(domain.getType())
                .isRead(domain.isRead())
                .sender(MemberJpaEntity.builder().id(domain.getSenderId().getValue()).build())
                .receiver(MemberJpaEntity.builder().id(domain.getReceiverId().getValue()).build())
                .build();
    }
}
