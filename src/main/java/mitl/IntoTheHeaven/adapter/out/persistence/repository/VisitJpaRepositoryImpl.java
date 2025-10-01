package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QVisitJpaEntity.visitJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QVisitMemberJpaEntity.visitMemberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QChurchMemberJpaEntity.churchMemberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QMemberJpaEntity.memberJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QPrayerJpaEntity.prayerJpaEntity;

@Repository
@RequiredArgsConstructor
public class VisitJpaRepositoryImpl implements VisitJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<VisitJpaEntity> findMyVisits(UUID churchMemberId) {
        return queryFactory
                .selectFrom(visitJpaEntity)
                .distinct()
                .leftJoin(visitJpaEntity.pastor, churchMemberJpaEntity).fetchJoin()
                .leftJoin(churchMemberJpaEntity.member, memberJpaEntity).fetchJoin()
                .leftJoin(visitJpaEntity.visitMembers, visitMemberJpaEntity).fetchJoin()
                .leftJoin(visitMemberJpaEntity.prayers, prayerJpaEntity).fetchJoin()
                .where(visitJpaEntity.pastor.id.eq(churchMemberId))
                .orderBy(visitJpaEntity.date.desc(), visitJpaEntity.startedAt.desc())
                .fetch();
    }
}

