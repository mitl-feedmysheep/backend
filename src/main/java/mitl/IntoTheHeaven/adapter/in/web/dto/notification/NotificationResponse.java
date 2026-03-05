package mitl.IntoTheHeaven.adapter.in.web.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class NotificationResponse {
    private final UUID id;
    private final String type;
    private final String description;
    private final String entityType;
    private final String entityId;
    private final String targetUrl;
    @JsonProperty("isRead")
    private final boolean isRead;
    private final LocalDateTime createdAt;

    @Builder
    public NotificationResponse(UUID id, String type, String description, String entityType, String entityId,
                                 String targetUrl, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
        this.targetUrl = targetUrl;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId().getValue())
                .type(notification.getType().getValue())
                .description(notification.getDescription())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static List<NotificationResponse> from(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
