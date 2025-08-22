package mitl.IntoTheHeaven.adapter.out.persistence;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.Prayer;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrayerPersistenceAdapter implements PrayerPort {

    //TODO ckdtn
    private final PrayerJpaRepository prayerJpaRepository;

    @Override
    public Integer findPrayerRequestCountByMemberIds(List<UUID> memberIds) {
        return prayerJpaRepository.findAllByMemberIdIn(memberIds)
                .stream()
                .map(this::toDomain)
                .map(Prayer::getPrayerRequest)
                .count();
    }

    private Prayer toDomain(PrayerJpaEntity prayerJpaEntity) {
        return Prayer.builder()
                .id(prayerJpaEntity.getId())
                .memberId(prayerJpaEntity.getMemberId())
                .prayerRequest(prayerJpaEntity.getPrayerRequest())
                .build();
    }
}
