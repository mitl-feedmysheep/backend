package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushSubscriptionJpaEntity;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.PushSubscriptionId;
import org.springframework.stereotype.Component;

@Component
public class PushSubscriptionPersistenceMapper {

    public PushSubscription toDomain(PushSubscriptionJpaEntity entity) {
        return PushSubscription.builder()
                .id(PushSubscriptionId.from(entity.getId()))
                .memberId(MemberId.from(entity.getMemberId()))
                .endpoint(entity.getEndpoint())
                .p256dh(entity.getP256dh())
                .auth(entity.getAuth())
                .userAgent(entity.getUserAgent())
                .timezone(entity.getTimezone())
                .build();
    }

    public PushSubscriptionJpaEntity toEntity(PushSubscription domain) {
        return PushSubscriptionJpaEntity.builder()
                .id(domain.getId().getValue())
                .memberId(domain.getMemberId().getValue())
                .endpoint(domain.getEndpoint())
                .p256dh(domain.getP256dh())
                .auth(domain.getAuth())
                .userAgent(domain.getUserAgent())
                .timezone(domain.getTimezone())
                .build();
    }
}
