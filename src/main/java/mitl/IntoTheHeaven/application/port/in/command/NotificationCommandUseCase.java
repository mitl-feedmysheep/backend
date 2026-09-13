package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.UUID;

public interface NotificationCommandUseCase {

    void markAsRead(UUID notificationId, MemberId receiverId);

    void markAllAsReadByEntity(MemberId receiverId, String entityType, String entityId);
}
