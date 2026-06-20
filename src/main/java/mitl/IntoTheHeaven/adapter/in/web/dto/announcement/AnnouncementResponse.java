package mitl.IntoTheHeaven.adapter.in.web.dto.announcement;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AnnouncementResponse {

    private final UUID id;
    private final String entityType;
    private final String entityId;
    private final String title;
    private final String body;
    private final LocalDateTime sendAt;
    private final boolean isSent;
    private final LocalDateTime createdAt;

    public static AnnouncementResponse from(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId().getValue())
                .entityType(announcement.getEntityType())
                .entityId(announcement.getEntityId())
                .title(announcement.getTitle())
                .body(announcement.getBody())
                .sendAt(announcement.getSendAt())
                .isSent(announcement.isSent())
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    public static List<AnnouncementResponse> from(List<Announcement> announcements) {
        return announcements.stream().map(AnnouncementResponse::from).toList();
    }
}
