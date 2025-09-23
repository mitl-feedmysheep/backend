package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MediaPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MediaJpaRepository;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MediaPersistenceAdapter implements MediaPort {

    private final MediaJpaRepository mediaJpaRepository;
    private final MediaPersistenceMapper mediaPersistenceMapper;

    @Override
    public Media save(Media media) {
        var entity = mediaPersistenceMapper.toEntity(media);
        var savedEntity = mediaJpaRepository.save(entity);
        return mediaPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<Media> saveAll(List<Media> medias) {
        var entities = mediaPersistenceMapper.toEntityList(medias);
        var savedEntities = mediaJpaRepository.saveAll(entities);
        return mediaPersistenceMapper.toDomainList(savedEntities);
    }

    @Override
    public Optional<Media> findById(MediaId mediaId) {
        return mediaJpaRepository.findById(mediaId.getValue())
                .map(mediaPersistenceMapper::toDomain);
    }

    @Override
    public List<Media> findByEntity(EntityType entityType, UUID entityId) {
        var entities = mediaJpaRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return mediaPersistenceMapper.toDomainList(entities);
    }

    @Override
    public Optional<Media> findByEntityAndType(EntityType entityType, UUID entityId, MediaType mediaType) {
        return mediaJpaRepository.findByEntityTypeAndEntityIdAndMediaType(entityType, entityId, mediaType)
                .map(mediaPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Media> findThumbnailByEntity(EntityType entityType, UUID entityId) {
        return mediaJpaRepository.findThumbnailByEntity(entityType, entityId)
                .map(mediaPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Media> findMainImageByEntity(EntityType entityType, UUID entityId) {
        return mediaJpaRepository.findMainImageByEntity(entityType, entityId)
                .map(mediaPersistenceMapper::toDomain);
    }

    @Override
    public List<Media> findAll() {
        var entities = mediaJpaRepository.findAll();
        return mediaPersistenceMapper.toDomainList(entities);
    }
}
