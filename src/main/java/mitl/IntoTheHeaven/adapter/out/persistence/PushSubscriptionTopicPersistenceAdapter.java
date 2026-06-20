package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushSubscriptionTopicJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PushSubscriptionTopicJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PushSubscriptionTopicPersistenceAdapter implements PushSubscriptionTopicPort {

    private final PushSubscriptionTopicJpaRepository repository;

    @Override
    @Transactional
    public void subscribe(MemberId memberId, PushTopic topic) {
        if (repository.existsByMemberIdAndTopic(memberId.getValue(), topic)) return;
        repository.save(new PushSubscriptionTopicJpaEntity(UUID.randomUUID(), memberId.getValue(), topic, null));
    }

    @Override
    @Transactional
    public void unsubscribe(MemberId memberId, PushTopic topic) {
        repository.deleteByMemberIdAndTopic(memberId.getValue(), topic);
    }

    @Override
    public boolean isSubscribed(MemberId memberId, PushTopic topic) {
        return repository.existsByMemberIdAndTopic(memberId.getValue(), topic);
    }

    @Override
    public List<PushTopic> findTopicsByMemberId(MemberId memberId) {
        return repository.findByMemberId(memberId.getValue())
                .stream()
                .map(PushSubscriptionTopicJpaEntity::getTopic)
                .toList();
    }

    @Override
    public List<MemberId> findMemberIdsByTopic(PushTopic topic) {
        return repository.findMemberIdByTopic(topic)
                .stream()
                .map(MemberId::from)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllByMemberId(MemberId memberId) {
        repository.deleteByMemberId(memberId.getValue());
    }
}
