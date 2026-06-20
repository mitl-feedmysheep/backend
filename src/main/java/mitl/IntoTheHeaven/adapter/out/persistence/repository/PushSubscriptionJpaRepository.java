package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscriptionJpaEntity, UUID> {

    List<PushSubscriptionJpaEntity> findByMemberId(UUID memberId);

    Optional<PushSubscriptionJpaEntity> findByEndpoint(String endpoint);

    boolean existsByEndpoint(String endpoint);

    List<PushSubscriptionJpaEntity> findByMemberIdIn(List<UUID> memberIds);

    @Modifying
    @Transactional
    void deleteByEndpoint(String endpoint);
}
