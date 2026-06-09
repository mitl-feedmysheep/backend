package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.PushSubscriptionPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PushSubscriptionJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PushSubscriptionPersistenceAdapter implements PushSubscriptionPort {

    private final PushSubscriptionJpaRepository repository;
    private final PushSubscriptionPersistenceMapper mapper;

    @Override
    public PushSubscription save(PushSubscription pushSubscription) {
        var entity = mapper.toEntity(pushSubscription);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<PushSubscription> findByMemberId(MemberId memberId) {
        return repository.findByMemberId(memberId.getValue())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PushSubscription> findByEndpoint(String endpoint) {
        return repository.findByEndpoint(endpoint).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEndpoint(String endpoint) {
        return repository.existsByEndpoint(endpoint);
    }

    @Override
    public List<PushSubscription> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByEndpoint(String endpoint) {
        repository.softDeleteByEndpoint(endpoint);
    }
}
