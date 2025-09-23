package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MediaJpaEntity;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MediaPersistenceMapper {

    /**
     * JPA Entity → Domain Model
     */
    public Media toDomain(MediaJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Media.builder()
                .id(MediaId.from(entity.getId()))
                .mediaType(entity.getMediaType())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .storagePath(entity.getStoragePath())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /**
     * Domain Model → JPA Entity
     */
    public MediaJpaEntity toEntity(Media domain) {
        if (domain == null) {
            return null;
        }

        return MediaJpaEntity.builder()
                .id(domain.getId().getValue()) // MANDATORY: Set domain ID
                .mediaType(domain.getMediaType())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .storagePath(domain.getStoragePath())
                .url(domain.getUrl())
                .createdAt(domain.getCreatedAt())
                .deletedAt(domain.getDeletedAt()) // soft delete 지원
                .build();
    }

    /**
     * JPA Entity List → Domain Model List
     */
    public List<Media> toDomainList(List<MediaJpaEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Domain Model List → JPA Entity List
     */
    public List<MediaJpaEntity> toEntityList(List<Media> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
