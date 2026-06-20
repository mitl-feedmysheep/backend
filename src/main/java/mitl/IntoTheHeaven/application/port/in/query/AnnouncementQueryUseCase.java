package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Announcement;

import java.util.List;
import java.util.UUID;

public interface AnnouncementQueryUseCase {

    List<Announcement> getRecent2(String entityType, String entityId);

    List<Announcement> getList(String entityType, String entityId);

    List<Announcement> getRecent2ByType(String entityType, String entityId, String type);

    List<Announcement> getListByType(String entityType, String entityId, String type);

    Announcement getById(UUID id);
}
