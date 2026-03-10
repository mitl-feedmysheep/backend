package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SendMessageCommand;
import mitl.IntoTheHeaven.application.port.out.MessagePort;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import mitl.IntoTheHeaven.domain.model.MessageId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageCommandServiceTest {

    @Mock
    private MessagePort messagePort;

    @InjectMocks
    private MessageCommandService messageCommandService;

    private MemberId senderId;
    private MemberId receiverId;

    @BeforeEach
    void setUp() {
        senderId = MemberId.from(UUID.randomUUID());
        receiverId = MemberId.from(UUID.randomUUID());
    }

    @Nested
    @DisplayName("sendMessage - 메시지 전송")
    class SendMessageTests {

        @Test
        @DisplayName("UUID가 생성되고 isRead=false로 메시지가 저장된다")
        void shouldCreateMessageWithIsReadFalse() {
            SendMessageCommand command = new SendMessageCommand(
                    senderId, receiverId, "안녕하세요", MessageType.NORMAL);

            when(messagePort.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

            Message result = messageCommandService.sendMessage(command);

            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(messagePort).save(captor.capture());
            Message saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getSenderId()).isEqualTo(senderId);
            assertThat(saved.getReceiverId()).isEqualTo(receiverId);
            assertThat(saved.getContent()).isEqualTo("안녕하세요");
            assertThat(saved.getType()).isEqualTo(MessageType.NORMAL);
            assertThat(saved.isRead()).isFalse();
        }

        @Test
        @DisplayName("BIRTHDAY 타입 메시지도 정상적으로 전송된다")
        void shouldCreateBirthdayMessage() {
            SendMessageCommand command = new SendMessageCommand(
                    senderId, receiverId, "생일 축하합니다!", MessageType.BIRTHDAY);

            when(messagePort.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

            messageCommandService.sendMessage(command);

            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(messagePort).save(captor.capture());
            assertThat(captor.getValue().getType()).isEqualTo(MessageType.BIRTHDAY);
        }
    }

    @Nested
    @DisplayName("markAsRead - 메시지 읽음 처리")
    class MarkAsReadTests {

        @Test
        @DisplayName("수신자가 요청하면 읽음 처리가 성공한다")
        void shouldMarkAsReadWhenReceiverRequests() {
            UUID messageUuid = UUID.randomUUID();
            Message message = Message.builder()
                    .id(MessageId.from(messageUuid))
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content("내용")
                    .type(MessageType.NORMAL)
                    .isRead(false)
                    .build();

            when(messagePort.findById(messageUuid)).thenReturn(Optional.of(message));

            messageCommandService.markAsRead(messageUuid, receiverId);

            verify(messagePort).markAsRead(messageUuid);
        }

        @Test
        @DisplayName("메시지가 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenMessageNotFound() {
            UUID messageUuid = UUID.randomUUID();
            when(messagePort.findById(messageUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> messageCommandService.markAsRead(messageUuid, receiverId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Message not found");

            verify(messagePort, never()).markAsRead(any());
        }

        @Test
        @DisplayName("수신자가 아닌 사람이 요청하면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenNonReceiverRequests() {
            UUID messageUuid = UUID.randomUUID();
            Message message = Message.builder()
                    .id(MessageId.from(messageUuid))
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content("내용")
                    .type(MessageType.NORMAL)
                    .isRead(false)
                    .build();

            MemberId otherMemberId = MemberId.from(UUID.randomUUID());
            when(messagePort.findById(messageUuid)).thenReturn(Optional.of(message));

            assertThatThrownBy(() -> messageCommandService.markAsRead(messageUuid, otherMemberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Only the receiver can mark the message as read");

            verify(messagePort, never()).markAsRead(any());
        }

        @Test
        @DisplayName("발신자가 읽음 처리를 시도하면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenSenderTriesToMarkAsRead() {
            UUID messageUuid = UUID.randomUUID();
            Message message = Message.builder()
                    .id(MessageId.from(messageUuid))
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content("내용")
                    .type(MessageType.NORMAL)
                    .isRead(false)
                    .build();

            when(messagePort.findById(messageUuid)).thenReturn(Optional.of(message));

            assertThatThrownBy(() -> messageCommandService.markAsRead(messageUuid, senderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Only the receiver can mark the message as read");
        }
    }
}
