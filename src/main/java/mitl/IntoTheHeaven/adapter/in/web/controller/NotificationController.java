package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.message.UnreadCountResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.notification.NotificationResponse;
import mitl.IntoTheHeaven.application.port.in.command.NotificationCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.NotificationQueryUseCase;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "APIs for Notification Management")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCommandUseCase notificationCommandUseCase;
    private final NotificationQueryUseCase notificationQueryUseCase;

    @Operation(summary = "Get My Notifications", description = "Retrieves all notifications for the current user. Optionally filter by department.")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal String memberId,
            @RequestParam(required = false) UUID departmentId) {
        DepartmentId deptId = departmentId != null ? DepartmentId.from(departmentId) : null;
        List<Notification> notifications = notificationQueryUseCase.getMyNotifications(
                MemberId.from(UUID.fromString(memberId)), deptId);
        return ResponseEntity.ok(NotificationResponse.from(notifications));
    }

    @Operation(summary = "Get Unread Count", description = "Returns the number of unread notifications for the current user. Optionally filter by department.")
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal String memberId,
            @RequestParam(required = false) UUID departmentId) {
        DepartmentId deptId = departmentId != null ? DepartmentId.from(departmentId) : null;
        long count = notificationQueryUseCase.getUnreadCount(
                MemberId.from(UUID.fromString(memberId)), deptId);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @Operation(summary = "Mark as Read", description = "Marks a specific notification as read.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable("notificationId") UUID notificationId,
            @AuthenticationPrincipal String memberId) {
        notificationCommandUseCase.markAsRead(notificationId,
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok().build();
    }
}
