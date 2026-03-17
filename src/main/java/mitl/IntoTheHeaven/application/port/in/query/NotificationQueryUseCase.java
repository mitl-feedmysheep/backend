package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;

import java.util.List;

public interface NotificationQueryUseCase {

    List<Notification> getMyNotifications(MemberId memberId, DepartmentId departmentId);

    long getUnreadCount(MemberId memberId, DepartmentId departmentId);
}
