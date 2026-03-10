package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    private final MessageId MESSAGE_ID = MessageId.from(UUID.randomUUID());
    private final MemberId SENDER_ID = MemberId.from(UUID.randomUUID());
    private final MemberId RECEIVER_ID = MemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 10, 0);

    private Message createMessage(boolean isRead) {
        return Message.builder()
                .id(MESSAGE_ID)
                .senderId(SENDER_ID)
                .senderName("홍길동")
                .receiverId(RECEIVER_ID)
                .receiverName("김철수")
                .content("생일 축하합니다!")
                .type(MessageType.BIRTHDAY)
                .isRead(isRead)
                .createdAt(CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("markAsRead")
    class MarkAsRead {

        @Test
        @DisplayName("읽음 처리 시 isRead가 true가 된다")
        void setsIsReadToTrue() {
            Message message = createMessage(false);

            Message read = message.markAsRead();

            assertThat(read.isRead()).isTrue();
        }

        @Test
        @DisplayName("이미 읽은 메시지를 다시 읽음 처리해도 true를 유지한다")
        void alreadyReadMessageRemainsRead() {
            Message message = createMessage(true);

            Message read = message.markAsRead();

            assertThat(read.isRead()).isTrue();
        }

        @Test
        @DisplayName("다른 필드들은 보존된다")
        void preservesOtherFields() {
            Message message = createMessage(false);

            Message read = message.markAsRead();

            assertThat(read.getId()).isEqualTo(MESSAGE_ID);
            assertThat(read.getSenderId()).isEqualTo(SENDER_ID);
            assertThat(read.getSenderName()).isEqualTo("홍길동");
            assertThat(read.getReceiverId()).isEqualTo(RECEIVER_ID);
            assertThat(read.getReceiverName()).isEqualTo("김철수");
            assertThat(read.getContent()).isEqualTo("생일 축하합니다!");
            assertThat(read.getType()).isEqualTo(MessageType.BIRTHDAY);
            assertThat(read.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Message message = createMessage(false);

            message.markAsRead();

            assertThat(message.isRead()).isFalse();
        }
    }
}
