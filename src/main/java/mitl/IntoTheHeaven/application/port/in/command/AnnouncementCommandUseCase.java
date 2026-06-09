package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.Announcement;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AnnouncementCommandUseCase {

    Announcement create(String entityType, String entityId, String title, String body, LocalDateTime sendAt);

    void delete(UUID id);
}
