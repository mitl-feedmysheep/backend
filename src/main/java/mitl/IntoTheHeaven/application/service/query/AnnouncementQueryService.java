package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.AnnouncementQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.AnnouncementPort;
import mitl.IntoTheHeaven.domain.model.Announcement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnnouncementQueryService implements AnnouncementQueryUseCase {

    private final AnnouncementPort announcementPort;

    @Override
    public List<Announcement> getRecent2(String entityType, String entityId) {
        return announcementPort.findTop2ByEntity(entityType, entityId);
    }

    @Override
    public List<Announcement> getList(String entityType, String entityId) {
        return announcementPort.findByEntity(entityType, entityId);
    }

    @Override
    public Announcement getById(UUID id) {
        return announcementPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found: " + id));
    }
}
