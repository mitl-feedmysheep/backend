package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushDeliveryLogJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PushDeliveryLogJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PushDeliveryLogPersistenceAdapter implements PushDeliveryLogPort {

    private final PushDeliveryLogJpaRepository repository;

    @Override
    public void save(PushDeliveryLog log) {
        repository.save(PushDeliveryLogJpaEntity.builder()
                .id(log.getId())
                .memberId(log.getMemberId().getValue())
                .subscriptionId(log.getSubscriptionId())
                .pushType(log.getPushType())
                .templateKey(log.getTemplateKey())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .sendResult(log.getSendResult())
                .sentAt(log.getSentAt())
                .clickedAt(log.getClickedAt())
                .build());
    }

    @Override
    public List<PushDeliveryLog> findByMemberIdsAndTypeSince(List<UUID> memberIds, PushType type, LocalDateTime since) {
        if (memberIds.isEmpty()) return List.of();
        return repository.findByMemberIdInAndPushTypeAndSentAtGreaterThanEqual(memberIds, type, since).stream()
                .map(e -> PushDeliveryLog.builder()
                        .id(e.getId())
                        .memberId(MemberId.from(e.getMemberId()))
                        .subscriptionId(e.getSubscriptionId())
                        .pushType(e.getPushType())
                        .templateKey(e.getTemplateKey())
                        .entityType(e.getEntityType())
                        .entityId(e.getEntityId())
                        .sendResult(e.getSendResult())
                        .sentAt(e.getSentAt())
                        .clickedAt(e.getClickedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public boolean markClicked(UUID deliveryId, LocalDateTime clickedAt) {
        return repository.markClicked(deliveryId, clickedAt) > 0;
    }
}
