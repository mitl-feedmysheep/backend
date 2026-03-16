package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPort {

    Notification save(Notification notification);

    List<Notification> findByReceiverId(UUID receiverId);

    List<Notification> findByReceiverIdAndDepartmentId(UUID receiverId, UUID departmentId);

    long countUnreadByReceiverId(UUID receiverId);

    long countUnreadByReceiverIdAndDepartmentId(UUID receiverId, UUID departmentId);

    Optional<Notification> findById(UUID notificationId);

    void markAsRead(UUID notificationId);

    boolean existsUnreadByReceiverAndTypeAndEntity(UUID receiverId, String type, String entityType, String entityId);
}
