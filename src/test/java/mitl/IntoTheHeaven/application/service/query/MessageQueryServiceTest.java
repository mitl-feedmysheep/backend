package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.MessagePort;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import mitl.IntoTheHeaven.domain.model.MessageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageQueryServiceTest {

    @Mock
    private MessagePort messagePort;

    @InjectMocks
    private MessageQueryService messageQueryService;

    private Message createMessage(MemberId senderId, MemberId receiverId, String content) {
        return Message.builder()
                .id(MessageId.from(UUID.randomUUID()))
                .senderId(senderId)
                .senderName("보낸사람")
                .receiverId(receiverId)
                .receiverName("받는사람")
                .content(content)
                .type(MessageType.NORMAL)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getMyMessages")
    class GetMyMessages {

        @Test
        @DisplayName("수신 메시지 목록 조회")
        void shouldReturnReceivedMessages() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            MemberId senderId = MemberId.from(UUID.randomUUID());

            List<Message> messages = List.of(
                    createMessage(senderId, memberId, "안녕하세요"),
                    createMessage(senderId, memberId, "생일 축하합니다")
            );

            when(messagePort.findByReceiverId(memberId.getValue())).thenReturn(messages);

            List<Message> result = messageQueryService.getMyMessages(memberId);

            assertThat(result).hasSize(2);
            verify(messagePort).findByReceiverId(memberId.getValue());
        }

        @Test
        @DisplayName("수신 메시지가 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoMessages() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(messagePort.findByReceiverId(memberId.getValue())).thenReturn(List.of());

            List<Message> result = messageQueryService.getMyMessages(memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUnreadCount")
    class GetUnreadCount {

        @Test
        @DisplayName("미읽음 메시지 수 조회")
        void shouldReturnUnreadCount() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(messagePort.countUnreadByReceiverId(memberId.getValue())).thenReturn(5L);

            long result = messageQueryService.getUnreadCount(memberId);

            assertThat(result).isEqualTo(5L);
            verify(messagePort).countUnreadByReceiverId(memberId.getValue());
        }

        @Test
        @DisplayName("미읽음 메시지가 없으면 0 반환")
        void shouldReturnZeroWhenAllRead() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(messagePort.countUnreadByReceiverId(memberId.getValue())).thenReturn(0L);

            long result = messageQueryService.getUnreadCount(memberId);

            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("getSentMessages")
    class GetSentMessages {

        @Test
        @DisplayName("발신 메시지 목록 조회")
        void shouldReturnSentMessages() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            MemberId receiverId = MemberId.from(UUID.randomUUID());

            List<Message> messages = List.of(
                    createMessage(memberId, receiverId, "보낸 메시지1"),
                    createMessage(memberId, receiverId, "보낸 메시지2")
            );

            when(messagePort.findBySenderId(memberId.getValue())).thenReturn(messages);

            List<Message> result = messageQueryService.getSentMessages(memberId);

            assertThat(result).hasSize(2);
            verify(messagePort).findBySenderId(memberId.getValue());
        }

        @Test
        @DisplayName("발신 메시지가 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoSentMessages() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(messagePort.findBySenderId(memberId.getValue())).thenReturn(List.of());

            List<Message> result = messageQueryService.getSentMessages(memberId);

            assertThat(result).isEmpty();
        }
    }
}
