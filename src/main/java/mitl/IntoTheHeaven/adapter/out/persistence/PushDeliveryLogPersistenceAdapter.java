package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushDeliveryLogJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PushDeliveryLogJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Transactional
    public boolean markClicked(UUID deliveryId, LocalDateTime clickedAt) {
        return repository.markClicked(deliveryId, clickedAt) > 0;
    }
}
