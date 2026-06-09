package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Announcement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementPort {

    Announcement save(Announcement announcement);

    List<Announcement> findTop2ByEntity(String entityType, String entityId);

    List<Announcement> findByEntity(String entityType, String entityId);

    Optional<Announcement> findById(UUID id);

    List<Announcement> findPendingToSend(LocalDateTime now);

    void markAsSent(UUID id);

    void delete(UUID id);
}
