package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Notification extends DomainEntity<Notification, NotificationId> {

    private final MemberId receiverId;
    private final MemberId senderId;
    private final NotificationType type;
    private final String description;
    private final String entityType;
    private final String entityId;
    private final String targetUrl;
    private final boolean isRead;
    private final LocalDateTime createdAt;

    public Notification markAsRead() {
        return this.toBuilder()
                .isRead(true)
                .build();
    }
}
