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
import mitl.IntoTheHeaven.application.port.out.PrayerPushCandidate;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitMemberJpaEntity;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
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
    private final EntityManager entityManager;

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
    public List<Prayer> findByGatheringMemberIds(List<UUID> gatheringMemberIds) {
        if (gatheringMemberIds.isEmpty()) return List.of();
        return prayerJpaRepository.findAllByGatheringMemberIdIn(gatheringMemberIds).stream()
                .map(this::toDomain)
                .toList();
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
                        prayerJpaEntity.gatheringMember.id,
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
                        .gatheringMemberId(tuple.get(prayerJpaEntity.gatheringMember.id) != null
                                ? GatheringMemberId.from(tuple.get(prayerJpaEntity.gatheringMember.id)) : null)
                        .groupName(tuple.get(groupJpaEntity.name))
                        .gatheringDate(tuple.get(gatheringJpaEntity.date))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<PrayerPushCandidate> findUnansweredForPush(List<UUID> memberIds, LocalDate oldestGatheringDate) {
        if (memberIds.isEmpty()) return List.of();
        List<Tuple> rows = queryFactory
                .select(
                        prayerJpaEntity.id,
                        prayerJpaEntity.member.id,
                        prayerJpaEntity.prayerRequest,
                        prayerJpaEntity.gatheringMember.id,
                        gatheringJpaEntity.date,
                        prayerJpaEntity.createdAt
                )
                .from(prayerJpaEntity)
                .leftJoin(prayerJpaEntity.gatheringMember, gatheringMemberJpaEntity)
                .leftJoin(gatheringMemberJpaEntity.gathering, gatheringJpaEntity)
                .where(
                        prayerJpaEntity.member.id.in(memberIds),
                        prayerJpaEntity.isAnswered.isFalse(),
                        prayerJpaEntity.visitMember.id.isNull(),
                        prayerJpaEntity.gatheringMember.id.isNull()
                                .or(gatheringJpaEntity.date.goe(oldestGatheringDate))
                )
                .fetch();

        return rows.stream()
                .filter(t -> t.get(prayerJpaEntity.prayerRequest) != null
                        && !t.get(prayerJpaEntity.prayerRequest).isBlank())
                .map(t -> new PrayerPushCandidate(
                        t.get(prayerJpaEntity.id),
                        t.get(prayerJpaEntity.member.id),
                        t.get(prayerJpaEntity.prayerRequest),
                        t.get(prayerJpaEntity.gatheringMember.id) == null,
                        t.get(gatheringJpaEntity.date),
                        t.get(prayerJpaEntity.createdAt)))
                .toList();
    }

    private Prayer toDomain(PrayerJpaEntity prayerJpaEntity) {
        return Prayer.builder()
                .id(PrayerId.from(prayerJpaEntity.getId()))
                .memberId(MemberId.from(prayerJpaEntity.getMember().getId()))
                .gatheringMemberId(prayerJpaEntity.getGatheringMember() != null
                        ? GatheringMemberId.from(prayerJpaEntity.getGatheringMember().getId()) : null)
                .visitMemberId(prayerJpaEntity.getVisitMember() != null
                        ? VisitMemberId.from(prayerJpaEntity.getVisitMember().getId()) : null)
                .prayerRequest(prayerJpaEntity.getPrayerRequest())
                .description(prayerJpaEntity.getDescription())
                .isAnswered(prayerJpaEntity.isAnswered())
                .createdAt(prayerJpaEntity.getCreatedAt())
                .deletedAt(prayerJpaEntity.getDeletedAt())
                .build();
    }

    private PrayerJpaEntity toEntity(Prayer prayer) {
        MemberJpaEntity memberRef = memberJpaRepository.getReferenceById(prayer.getMemberId().getValue());
        // 나의 기도는 gathering/visit 연결이 없다. 기존 연결은 저장 시 유지해야 한다 (visit 연결이 지워지던 문제 수정)
        GatheringMemberJpaEntity gatheringMemberRef = prayer.getGatheringMemberId() != null
                ? gatheringMemberJpaRepository.getReferenceById(prayer.getGatheringMemberId().getValue())
                : null;
        VisitMemberJpaEntity visitMemberRef = prayer.getVisitMemberId() != null
                ? entityManager.getReference(VisitMemberJpaEntity.class, prayer.getVisitMemberId().getValue())
                : null;
        return PrayerJpaEntity.builder()
                .id(prayer.getId().getValue())
                .gatheringMember(gatheringMemberRef)
                .visitMember(visitMemberRef)
                .member(memberRef)
                .prayerRequest(prayer.getPrayerRequest())
                .description(prayer.getDescription())
                .isAnswered(prayer.isAnswered())
                .deletedAt(prayer.getDeletedAt())
                .build();
    }
}
