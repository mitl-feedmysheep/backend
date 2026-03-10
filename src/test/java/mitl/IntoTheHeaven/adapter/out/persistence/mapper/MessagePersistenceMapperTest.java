package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MessageJpaEntity;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import mitl.IntoTheHeaven.domain.model.MessageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessagePersistenceMapperTest {

    private final MessagePersistenceMapper mapper = new MessagePersistenceMapper();

    @Test
    @DisplayName("toDomain: null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain: entity.message가 domain.content로 매핑되고, sender/receiver 이름이 추출된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        MemberJpaEntity sender = MemberJpaEntity.builder()
                .id(senderId)
                .name("보내는사람")
                .build();
        MemberJpaEntity receiver = MemberJpaEntity.builder()
                .id(receiverId)
                .name("받는사람")
                .build();

        MessageJpaEntity entity = MessageJpaEntity.builder()
                .id(id)
                .sender(sender)
                .receiver(receiver)
                .message("생일 축하합니다!")
                .type(MessageType.BIRTHDAY)
                .isRead(false)
                .createdAt(now)
                .deletedAt(now.plusDays(30))
                .build();

        Message domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getSenderId().getValue()).isEqualTo(senderId);
        assertThat(domain.getSenderName()).isEqualTo("보내는사람");
        assertThat(domain.getReceiverId().getValue()).isEqualTo(receiverId);
        assertThat(domain.getReceiverName()).isEqualTo("받는사람");
        assertThat(domain.getContent()).isEqualTo("생일 축하합니다!");
        assertThat(domain.getType()).isEqualTo(MessageType.BIRTHDAY);
        assertThat(domain.isRead()).isFalse();
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(30));
    }

    @Test
    @DisplayName("toEntity: null 입력 시 null을 반환한다")
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: domain.content가 entity.message로 매핑되고, stub MemberJpaEntity가 생성된다")
    void toEntity() {
        UUID id = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Message domain = Message.builder()
                .id(MessageId.from(id))
                .senderId(MemberId.from(senderId))
                .senderName("홍길동")
                .receiverId(MemberId.from(receiverId))
                .receiverName("김영희")
                .content("안녕하세요!")
                .type(MessageType.NORMAL)
                .isRead(true)
                .build();

        MessageJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getMessage()).isEqualTo("안녕하세요!");
        assertThat(entity.getType()).isEqualTo(MessageType.NORMAL);
        assertThat(entity.isRead()).isTrue();
        assertThat(entity.getSender().getId()).isEqualTo(senderId);
        assertThat(entity.getReceiver().getId()).isEqualTo(receiverId);
    }

    @Test
    @DisplayName("필드명 불일치 라운드트립: content <-> message 매핑이 양방향으로 올바르게 동작한다")
    void fieldNameMismatch_roundtrip() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        MemberJpaEntity sender = MemberJpaEntity.builder().id(senderId).name("A").build();
        MemberJpaEntity receiver = MemberJpaEntity.builder().id(receiverId).name("B").build();

        MessageJpaEntity original = MessageJpaEntity.builder()
                .id(UUID.randomUUID())
                .sender(sender)
                .receiver(receiver)
                .message("원본 메시지")
                .type(MessageType.NORMAL)
                .isRead(false)
                .build();

        Message domain = mapper.toDomain(original);
        assertThat(domain.getContent()).isEqualTo("원본 메시지");

        MessageJpaEntity backToEntity = mapper.toEntity(domain);
        assertThat(backToEntity.getMessage()).isEqualTo("원본 메시지");
    }
}
