package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Announcement extends DomainEntity<Announcement, AnnouncementId> {

    private final String entityType;
    private final String entityId;
    private final String title;
    private final String body;
    private final LocalDateTime sendAt;
    private final boolean isSent;
    private final boolean pushEnabled;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
