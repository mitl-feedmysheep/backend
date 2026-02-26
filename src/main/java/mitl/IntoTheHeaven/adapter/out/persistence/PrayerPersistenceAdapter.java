package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.core.Tuple;
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
import java.util.stream.Collectors;

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

    @Override
    public List<Prayer> findAllByMemberId(UUID memberId) {
        List<Tuple> results = queryFactory
                .select(
                        prayerJpaEntity.id,
                        prayerJpaEntity.prayerRequest,
                        prayerJpaEntity.description,
                        prayerJpaEntity.isAnswered,
                        prayerJpaEntity.createdAt,
                        prayerJpaEntity.deletedAt,
                        gatheringJpaEntity.date,
                        groupJpaEntity.name
                )
                .from(prayerJpaEntity)
                .leftJoin(prayerJpaEntity.gatheringMember, gatheringMemberJpaEntity)
                .leftJoin(gatheringMemberJpaEntity.gathering, gatheringJpaEntity)
                .leftJoin(gatheringJpaEntity.group, groupJpaEntity)
                // Exclude prayers created during visits (visit_member_id is not null)
                // so that only personal and gathering prayers are shown in "My Prayers" tab
                .where(
                        prayerJpaEntity.member.id.eq(memberId)
                                .and(prayerJpaEntity.visitMember.isNull())
                )
                .orderBy(prayerJpaEntity.createdAt.desc())
                .fetch();

        return results.stream()
                .map(tuple -> (Prayer) Prayer.builder()
                        .id(PrayerId.from(tuple.get(prayerJpaEntity.id)))
                        .memberId(MemberId.from(memberId))
                        .prayerRequest(tuple.get(prayerJpaEntity.prayerRequest))
                        .description(tuple.get(prayerJpaEntity.description))
                        .isAnswered(Boolean.TRUE.equals(tuple.get(prayerJpaEntity.isAnswered)))
                        .createdAt(tuple.get(prayerJpaEntity.createdAt))
                        .deletedAt(tuple.get(prayerJpaEntity.deletedAt))
                        .groupName(tuple.get(groupJpaEntity.name))
                        .gatheringDate(tuple.get(gatheringJpaEntity.date))
                        .build())
                .collect(Collectors.toList());
    }

    private Prayer toDomain(PrayerJpaEntity prayerJpaEntity) {
        return Prayer.builder()
                .id(PrayerId.from(prayerJpaEntity.getId()))
                .memberId(MemberId.from(prayerJpaEntity.getMember().getId()))
                .gatheringMemberId(prayerJpaEntity.getGatheringMember() != null
                        ? GatheringMemberId.from(prayerJpaEntity.getGatheringMember().getId()) : null)
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
