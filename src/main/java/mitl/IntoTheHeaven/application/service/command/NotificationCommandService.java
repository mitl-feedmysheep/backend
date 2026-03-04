package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.NotificationCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService implements NotificationCommandUseCase {

    private final NotificationPort notificationPort;

    @Override
    public void markAsRead(UUID notificationId, MemberId receiverId) {
        Notification notification = notificationPort.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!notification.getReceiverId().equals(receiverId)) {
            throw new IllegalArgumentException("Only the receiver can mark the notification as read");
        }

        notificationPort.markAsRead(notificationId);
    }
}
