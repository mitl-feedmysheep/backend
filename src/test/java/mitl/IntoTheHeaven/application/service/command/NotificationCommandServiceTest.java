package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private NotificationCommandService notificationCommandService;

    private UUID notificationUuid;
    private UUID receiverUuid;
    private MemberId receiverId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationUuid = UUID.randomUUID();
        receiverUuid = UUID.randomUUID();
        receiverId = MemberId.from(receiverUuid);

        notification = Notification.builder()
                .id(NotificationId.from(notificationUuid))
                .receiverId(receiverId)
                .senderId(null)
                .type(NotificationType.ADMIN_COMMENT)
                .entityType("GATHERING")
                .entityId(UUID.randomUUID().toString())
                .targetUrl("/groups/123/gathering/456")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("읽음 처리 - 수신자 본인이 요청하면 성공")
    void markAsRead_success() {
        when(notificationPort.findById(notificationUuid)).thenReturn(Optional.of(notification));

        notificationCommandService.markAsRead(notificationUuid, receiverId);

        verify(notificationPort).markAsRead(notificationUuid);
    }

    @Test
    @DisplayName("읽음 처리 - 알림이 존재하지 않으면 예외 발생")
    void markAsRead_notFound() {
        when(notificationPort.findById(notificationUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationCommandService.markAsRead(notificationUuid, receiverId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Notification not found");
    }

    @Test
    @DisplayName("읽음 처리 - 수신자가 아닌 다른 사람이 요청하면 예외 발생")
    void markAsRead_notReceiver() {
        when(notificationPort.findById(notificationUuid)).thenReturn(Optional.of(notification));

        MemberId otherMemberId = MemberId.from(UUID.randomUUID());

        assertThatThrownBy(() -> notificationCommandService.markAsRead(notificationUuid, otherMemberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only the receiver can mark the notification as read");

        verify(notificationPort, never()).markAsRead(any());
    }
}
