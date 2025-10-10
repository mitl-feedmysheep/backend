package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;

import java.util.List;
import java.util.UUID;

public interface VisitJpaRepositoryCustom {

    /**
     * Find all visits where the church member participated (ordered by date desc)
     */
    List<VisitJpaEntity> findMyVisits(UUID churchMemberId);

    /**
     * Find all visits by church ID and member ID where the member participated
     */
    List<VisitJpaEntity> findAllByChurchIdAndMemberId(UUID churchId, UUID memberId);
}

