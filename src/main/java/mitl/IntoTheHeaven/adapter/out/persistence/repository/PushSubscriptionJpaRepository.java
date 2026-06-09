package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionJpaRepository extends JpaRepository<PushSubscriptionJpaEntity, UUID> {

    List<PushSubscriptionJpaEntity> findByMemberId(UUID memberId);

    Optional<PushSubscriptionJpaEntity> findByEndpoint(String endpoint);

    boolean existsByEndpoint(String endpoint);

    @Modifying
    @Query(value = "UPDATE push_subscription SET deleted_at = NOW() WHERE endpoint = :endpoint AND deleted_at IS NULL", nativeQuery = true)
    void softDeleteByEndpoint(@Param("endpoint") String endpoint);
}
