package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushSubscriptionTopicJpaEntity;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PushSubscriptionTopicJpaRepository extends JpaRepository<PushSubscriptionTopicJpaEntity, UUID> {

    List<PushSubscriptionTopicJpaEntity> findByMemberId(UUID memberId);

    List<UUID> findMemberIdByTopic(PushTopic topic);

    boolean existsByMemberIdAndTopic(UUID memberId, PushTopic topic);

    void deleteByMemberIdAndTopic(UUID memberId, PushTopic topic);

    void deleteByMemberId(UUID memberId);
}
