package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.AnnouncementJpaEntity;
import mitl.IntoTheHeaven.domain.model.Announcement;
import mitl.IntoTheHeaven.domain.model.AnnouncementId;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementPersistenceMapper {

    public Announcement toDomain(AnnouncementJpaEntity entity) {
        if (entity == null) return null;
        return Announcement.builder()
                .id(AnnouncementId.from(entity.getId()))
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .sendAt(entity.getSendAt())
                .isSent(entity.isSent())
                .pushEnabled(entity.isPushEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AnnouncementJpaEntity toEntity(Announcement domain) {
        if (domain == null) return null;
        return AnnouncementJpaEntity.builder()
                .id(domain.getId().getValue())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .title(domain.getTitle())
                .body(domain.getBody())
                .sendAt(domain.getSendAt())
                .isSent(domain.isSent())
                .pushEnabled(domain.isPushEnabled())
                .build();
    }
}
