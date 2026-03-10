package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    private final NotificationId NOTIFICATION_ID = NotificationId.from(UUID.randomUUID());
    private final MemberId RECEIVER_ID = MemberId.from(UUID.randomUUID());
    private final MemberId SENDER_ID = MemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 10, 0);

    private Notification createNotification(boolean isRead) {
        return Notification.builder()
                .id(NOTIFICATION_ID)
                .receiverId(RECEIVER_ID)
                .senderId(SENDER_ID)
                .type(NotificationType.ADMIN_COMMENT)
                .description("관리자가 코멘트를 남겼습니다")
                .entityType("GATHERING")
                .entityId(UUID.randomUUID().toString())
                .targetUrl("/gatherings/123")
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
            Notification notification = createNotification(false);

            Notification read = notification.markAsRead();

            assertThat(read.isRead()).isTrue();
        }

        @Test
        @DisplayName("이미 읽은 알림을 다시 읽음 처리해도 true를 유지한다")
        void alreadyReadNotificationRemainsRead() {
            Notification notification = createNotification(true);

            Notification read = notification.markAsRead();

            assertThat(read.isRead()).isTrue();
        }

        @Test
        @DisplayName("다른 필드들은 보존된다")
        void preservesOtherFields() {
            Notification notification = createNotification(false);

            Notification read = notification.markAsRead();

            assertThat(read.getId()).isEqualTo(NOTIFICATION_ID);
            assertThat(read.getReceiverId()).isEqualTo(RECEIVER_ID);
            assertThat(read.getSenderId()).isEqualTo(SENDER_ID);
            assertThat(read.getType()).isEqualTo(NotificationType.ADMIN_COMMENT);
            assertThat(read.getDescription()).isEqualTo("관리자가 코멘트를 남겼습니다");
            assertThat(read.getEntityType()).isEqualTo("GATHERING");
            assertThat(read.getTargetUrl()).isEqualTo("/gatherings/123");
            assertThat(read.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Notification notification = createNotification(false);

            notification.markAsRead();

            assertThat(notification.isRead()).isFalse();
        }
    }
}
