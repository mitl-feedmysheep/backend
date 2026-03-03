package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Test
    @DisplayName("내 알림 목록 조회")
    void getMyNotifications() {
        UUID memberUuid = UUID.randomUUID();
        MemberId memberId = MemberId.from(memberUuid);

        Notification n1 = Notification.builder()
                .id(NotificationId.from(UUID.randomUUID()))
                .receiverId(memberId)
                .type(NotificationType.ADMIN_COMMENT)
                .entityType("GATHERING")
                .entityId(UUID.randomUUID().toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification n2 = Notification.builder()
                .id(NotificationId.from(UUID.randomUUID()))
                .receiverId(memberId)
                .type(NotificationType.ADMIN_COMMENT)
                .entityType("GATHERING")
                .entityId(UUID.randomUUID().toString())
                .isRead(true)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(notificationPort.findByReceiverId(memberUuid)).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationQueryService.getMyNotifications(memberId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isRead()).isFalse();
        assertThat(result.get(1).isRead()).isTrue();
    }

    @Test
    @DisplayName("미읽음 알림 개수 조회")
    void getUnreadCount() {
        UUID memberUuid = UUID.randomUUID();
        MemberId memberId = MemberId.from(memberUuid);

        when(notificationPort.countUnreadByReceiverId(memberUuid)).thenReturn(3L);

        long count = notificationQueryService.getUnreadCount(memberId);

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("알림이 없으면 빈 목록 반환")
    void getMyNotifications_empty() {
        UUID memberUuid = UUID.randomUUID();
        MemberId memberId = MemberId.from(memberUuid);

        when(notificationPort.findByReceiverId(memberUuid)).thenReturn(List.of());

        List<Notification> result = notificationQueryService.getMyNotifications(memberId);

        assertThat(result).isEmpty();
    }
}
