package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPort {

    Notification save(Notification notification);

    List<Notification> findByReceiverId(UUID receiverId);

    long countUnreadByReceiverId(UUID receiverId);

    Optional<Notification> findById(UUID notificationId);

    void markAsRead(UUID notificationId);

    boolean existsUnreadByReceiverAndTypeAndEntity(UUID receiverId, String type, String entityType, String entityId);
}
