package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;

import java.util.List;

public interface NotificationQueryUseCase {

    List<Notification> getMyNotifications(MemberId memberId);

    long getUnreadCount(MemberId memberId);
}
