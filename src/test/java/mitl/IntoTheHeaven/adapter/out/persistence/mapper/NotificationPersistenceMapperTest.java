package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.NotificationJpaEntity;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPersistenceMapperTest {

    private final NotificationPersistenceMapper mapper = new NotificationPersistenceMapper();

    @Test
    @DisplayName("JPA Entity -> Domain 변환")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID receiverUuid = UUID.randomUUID();
        UUID senderUuid = UUID.randomUUID();
        String entityId = UUID.randomUUID().toString();

        MemberJpaEntity receiver = MemberJpaEntity.builder().id(receiverUuid).build();
        MemberJpaEntity sender = MemberJpaEntity.builder().id(senderUuid).build();

        NotificationJpaEntity entity = NotificationJpaEntity.builder()
                .id(id)
                .receiver(receiver)
                .sender(sender)
                .type("ADMIN_COMMENT")
                .description("청년 1조 · 1월 15일")
                .entityType("GATHERING")
                .entityId(entityId)
                .targetUrl("/groups/abc/gathering/def")
                .isRead(false)
                .build();

        Notification domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getReceiverId().getValue()).isEqualTo(receiverUuid);
        assertThat(domain.getSenderId().getValue()).isEqualTo(senderUuid);
        assertThat(domain.getType()).isEqualTo(NotificationType.ADMIN_COMMENT);
        assertThat(domain.getDescription()).isEqualTo("청년 1조 · 1월 15일");
        assertThat(domain.getEntityType()).isEqualTo("GATHERING");
        assertThat(domain.getEntityId()).isEqualTo(entityId);
        assertThat(domain.getTargetUrl()).isEqualTo("/groups/abc/gathering/def");
        assertThat(domain.isRead()).isFalse();
    }

    @Test
    @DisplayName("JPA Entity -> Domain 변환 (sender가 null인 경우)")
    void toDomain_nullSender() {
        UUID id = UUID.randomUUID();
        UUID receiverUuid = UUID.randomUUID();

        MemberJpaEntity receiver = MemberJpaEntity.builder().id(receiverUuid).build();

        NotificationJpaEntity entity = NotificationJpaEntity.builder()
                .id(id)
                .receiver(receiver)
                .sender(null)
                .type("ADMIN_COMMENT")
                .entityType("GATHERING")
                .entityId("entity-123")
                .isRead(true)
                .build();

        Notification domain = mapper.toDomain(entity);

        assertThat(domain.getSenderId()).isNull();
        assertThat(domain.isRead()).isTrue();
    }

    @Test
    @DisplayName("Domain -> JPA Entity 변환")
    void toEntity() {
        UUID id = UUID.randomUUID();
        UUID receiverUuid = UUID.randomUUID();
        UUID senderUuid = UUID.randomUUID();
        String entityId = UUID.randomUUID().toString();

        Notification domain = Notification.builder()
                .id(NotificationId.from(id))
                .receiverId(MemberId.from(receiverUuid))
                .senderId(MemberId.from(senderUuid))
                .type(NotificationType.ADMIN_COMMENT)
                .description("청년 1조 · 1월 15일")
                .entityType("GATHERING")
                .entityId(entityId)
                .targetUrl("/groups/abc/gathering/def")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getReceiver().getId()).isEqualTo(receiverUuid);
        assertThat(entity.getSender().getId()).isEqualTo(senderUuid);
        assertThat(entity.getType()).isEqualTo("ADMIN_COMMENT");
        assertThat(entity.getDescription()).isEqualTo("청년 1조 · 1월 15일");
        assertThat(entity.getEntityType()).isEqualTo("GATHERING");
        assertThat(entity.getEntityId()).isEqualTo(entityId);
        assertThat(entity.getTargetUrl()).isEqualTo("/groups/abc/gathering/def");
        assertThat(entity.isRead()).isFalse();
    }

    @Test
    @DisplayName("Domain -> JPA Entity 변환 (sender가 null인 경우)")
    void toEntity_nullSender() {
        Notification domain = Notification.builder()
                .id(NotificationId.from(UUID.randomUUID()))
                .receiverId(MemberId.from(UUID.randomUUID()))
                .senderId(null)
                .type(NotificationType.ADMIN_COMMENT)
                .entityType("GATHERING")
                .entityId("entity-123")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getSender()).isNull();
    }

    @Test
    @DisplayName("description이 null인 경우 양방향 매핑 정상 동작")
    void description_null_roundtrip() {
        UUID id = UUID.randomUUID();
        MemberJpaEntity receiver = MemberJpaEntity.builder().id(UUID.randomUUID()).build();

        NotificationJpaEntity entity = NotificationJpaEntity.builder()
                .id(id)
                .receiver(receiver)
                .sender(null)
                .type("ADMIN_COMMENT")
                .description(null)
                .entityType("GATHERING")
                .entityId("entity-123")
                .isRead(false)
                .build();

        Notification domain = mapper.toDomain(entity);
        assertThat(domain.getDescription()).isNull();

        NotificationJpaEntity backToEntity = mapper.toEntity(domain);
        assertThat(backToEntity.getDescription()).isNull();
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }
}
