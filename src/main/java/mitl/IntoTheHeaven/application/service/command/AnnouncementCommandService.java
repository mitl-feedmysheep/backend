package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.AnnouncementCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.AnnouncementPort;
import mitl.IntoTheHeaven.domain.model.Announcement;
import mitl.IntoTheHeaven.domain.model.AnnouncementId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementCommandService implements AnnouncementCommandUseCase {

    private final AnnouncementPort announcementPort;

    @Override
    public Announcement create(String entityType, String entityId, String title, String body, LocalDateTime sendAt) {
        Announcement announcement = Announcement.builder()
                .id(AnnouncementId.from(UUID.randomUUID()))
                .entityType(entityType)
                .entityId(entityId)
                .title(title)
                .body(body)
                .sendAt(sendAt)
                .isSent(false)
                .build();
        return announcementPort.save(announcement);
    }

    @Override
    public void delete(UUID id) {
        announcementPort.delete(id);
    }
}
