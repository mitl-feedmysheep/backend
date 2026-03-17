package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.NotificationQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService implements NotificationQueryUseCase {

    private final NotificationPort notificationPort;

    @Override
    public List<Notification> getMyNotifications(MemberId memberId, DepartmentId departmentId) {
        if (departmentId != null) {
            return notificationPort.findByReceiverIdAndDepartmentId(memberId.getValue(), departmentId.getValue());
        }
        return notificationPort.findByReceiverId(memberId.getValue());
    }

    @Override
    public long getUnreadCount(MemberId memberId, DepartmentId departmentId) {
        if (departmentId != null) {
            return notificationPort.countUnreadByReceiverIdAndDepartmentId(memberId.getValue(), departmentId.getValue());
        }
        return notificationPort.countUnreadByReceiverId(memberId.getValue());
    }
}
