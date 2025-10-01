package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.PrayerJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GatheringMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QPrayerJpaEntity.prayerJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGatheringMemberJpaEntity.gatheringMemberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGatheringJpaEntity.gatheringJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGroupJpaEntity.groupJpaEntity;

@Component
@RequiredArgsConstructor
public class PrayerPersistenceAdapter implements PrayerPort {

    private final PrayerJpaRepository prayerJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final GatheringMemberJpaRepository gatheringMemberJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public long findPrayerRequestCountByMemberIds(List<UUID> memberIds) {
        return prayerJpaRepository.findAllByMemberIdIn(memberIds)
                .stream()
                .map(this::toDomain)
                .map(Prayer::getPrayerRequest)
                .count();
    }

    @Override
    public Long findPrayerRequestCountByChurchId(UUID churchId) {
        return queryFactory
                .select(prayerJpaEntity.count())
                .from(prayerJpaEntity)
                .innerJoin(prayerJpaEntity.gatheringMember, gatheringMemberJpaEntity)
                .innerJoin(gatheringMemberJpaEntity.gathering, gatheringJpaEntity)
                .innerJoin(gatheringJpaEntity.group, groupJpaEntity)
                .where(groupJpaEntity.church.id.eq(churchId))
                .fetchOne();
    }

    @Override
    public Optional<Prayer> findById(UUID prayerId) {
        return prayerJpaRepository.findById(prayerId).map(this::toDomain);
    }

    @Override
    public Prayer save(Prayer prayer) {
        PrayerJpaEntity entity = toEntity(prayer);
        PrayerJpaEntity saved = prayerJpaRepository.save(entity);
        return toDomain(saved);
    }

    private Prayer toDomain(PrayerJpaEntity prayerJpaEntity) {
        return Prayer.builder()
                .id(PrayerId.from(prayerJpaEntity.getId()))
                .memberId(MemberId.from(prayerJpaEntity.getMember().getId()))
                .gatheringMemberId(GatheringMemberId.from(prayerJpaEntity.getGatheringMember().getId()))
                .prayerRequest(prayerJpaEntity.getPrayerRequest())
                .description(prayerJpaEntity.getDescription())
                .isAnswered(prayerJpaEntity.isAnswered())
                .createdAt(prayerJpaEntity.getCreatedAt())
                .deletedAt(prayerJpaEntity.getDeletedAt())
                .build();
    }

    private PrayerJpaEntity toEntity(Prayer prayer) {
        MemberJpaEntity memberRef = memberJpaRepository.getReferenceById(prayer.getMemberId().getValue());
        GatheringMemberJpaEntity gatheringMemberRef = gatheringMemberJpaRepository
                .getReferenceById(prayer.getGatheringMemberId().getValue());
        return PrayerJpaEntity.builder()
                .id(prayer.getId().getValue())
                .gatheringMember(gatheringMemberRef)
                .member(memberRef)
                .prayerRequest(prayer.getPrayerRequest())
                .description(prayer.getDescription())
                .isAnswered(prayer.isAnswered())
                .deletedAt(prayer.getDeletedAt())
                .build();
    }
}
