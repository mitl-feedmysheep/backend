package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushDeliveryLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mitl.IntoTheHeaven.domain.enums.PushType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PushDeliveryLogJpaRepository extends JpaRepository<PushDeliveryLogJpaEntity, UUID> {

    /** idx_pdl_member_type_sent (member_id, push_type, sent_at) 사용 */
    List<PushDeliveryLogJpaEntity> findByMemberIdInAndPushTypeAndSentAtGreaterThanEqual(
            List<UUID> memberIds, PushType pushType, LocalDateTime since);

    @Modifying
    @Query("UPDATE PushDeliveryLogJpaEntity l SET l.clickedAt = :clickedAt WHERE l.id = :id AND l.clickedAt IS NULL")
    int markClicked(@Param("id") UUID id, @Param("clickedAt") LocalDateTime clickedAt);
}
