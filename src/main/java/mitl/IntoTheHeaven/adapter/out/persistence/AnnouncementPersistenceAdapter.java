package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.AnnouncementPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.AnnouncementJpaRepository;
import mitl.IntoTheHeaven.application.port.out.AnnouncementPort;
import mitl.IntoTheHeaven.domain.model.Announcement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnnouncementPersistenceAdapter implements AnnouncementPort {

    private final AnnouncementJpaRepository repository;
    private final AnnouncementPersistenceMapper mapper;

    @Override
    public Announcement save(Announcement announcement) {
        return mapper.toDomain(repository.save(mapper.toEntity(announcement)));
    }

    @Override
    public List<Announcement> findTop2ByEntity(String entityType, String entityId) {
        return repository.findTop2ByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Announcement> findByEntity(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Announcement> findTop2ByEntityAndType(String entityType, String entityId, String type) {
        return repository.findTop2ByEntityTypeAndEntityIdAndTypeOrderByCreatedAtDesc(entityType, entityId, type)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Announcement> findByEntityAndType(String entityType, String entityId, String type) {
        return repository.findByEntityTypeAndEntityIdAndTypeOrderByCreatedAtDesc(entityType, entityId, type)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Announcement> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Announcement> findPendingToSend(LocalDateTime now) {
        return repository.findBySendAtBeforeAndIsSentFalse(now)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void markAsSent(UUID id) {
        repository.findById(id).ifPresent(entity -> {
            entity.markAsSent();
            repository.save(entity);
        });
    }

    @Override
    public void delete(UUID id) {
        repository.softDeleteById(id);
    }
}
