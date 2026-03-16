package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.NotificationId;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Nested
    @DisplayName("getMyNotifications")
    class GetMyNotifications {

        @Test
        @DisplayName("departmentId가 null이면 전체 알림 조회")
        void withoutDepartmentId() {
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

            List<Notification> result = notificationQueryService.getMyNotifications(memberId, null);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).isRead()).isFalse();
            assertThat(result.get(1).isRead()).isTrue();
        }

        @Test
        @DisplayName("departmentId가 있으면 해당 부서 알림만 조회")
        void withDepartmentId() {
            UUID memberUuid = UUID.randomUUID();
            UUID departmentUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            DepartmentId departmentId = DepartmentId.from(departmentUuid);

            Notification n1 = Notification.builder()
                    .id(NotificationId.from(UUID.randomUUID()))
                    .receiverId(memberId)
                    .departmentId(departmentId)
                    .type(NotificationType.ADMIN_COMMENT)
                    .entityType("GATHERING")
                    .entityId(UUID.randomUUID().toString())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(notificationPort.findByReceiverIdAndDepartmentId(memberUuid, departmentUuid))
                    .thenReturn(List.of(n1));

            List<Notification> result = notificationQueryService.getMyNotifications(memberId, departmentId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDepartmentId()).isEqualTo(departmentId);
        }

        @Test
        @DisplayName("알림이 없으면 빈 목록 반환")
        void empty() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);

            when(notificationPort.findByReceiverId(memberUuid)).thenReturn(List.of());

            List<Notification> result = notificationQueryService.getMyNotifications(memberId, null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUnreadCount")
    class GetUnreadCount {

        @Test
        @DisplayName("departmentId가 null이면 전체 미읽음 개수")
        void withoutDepartmentId() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);

            when(notificationPort.countUnreadByReceiverId(memberUuid)).thenReturn(3L);

            long count = notificationQueryService.getUnreadCount(memberId, null);

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("departmentId가 있으면 해당 부서 미읽음 개수")
        void withDepartmentId() {
            UUID memberUuid = UUID.randomUUID();
            UUID departmentUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            DepartmentId departmentId = DepartmentId.from(departmentUuid);

            when(notificationPort.countUnreadByReceiverIdAndDepartmentId(memberUuid, departmentUuid))
                    .thenReturn(1L);

            long count = notificationQueryService.getUnreadCount(memberId, departmentId);

            assertThat(count).isEqualTo(1);
        }
    }
}
