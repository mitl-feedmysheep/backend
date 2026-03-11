package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.application.port.out.HomeSummaryData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface GatheringJpaRepository extends JpaRepository<GatheringJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"medias"})
    List<GatheringJpaEntity> findAllByGroupId(UUID groupId);

    @EntityGraph(attributePaths = {
            "group",
            "gatheringMembers",
            "gatheringMembers.groupMember",
            "gatheringMembers.groupMember.member",
            "gatheringMembers.prayers"
    })
    Optional<GatheringJpaEntity> findWithDetailsById(UUID id);

    @Query("SELECT new mitl.IntoTheHeaven.application.port.out.HomeSummaryData(" +
            "gm.id, gm.goal, g.date, grp.name) " +
            "FROM GatheringMemberJpaEntity gm " +
            "JOIN gm.gathering g " +
            "JOIN gm.groupMember grpm " +
            "JOIN g.group grp " +
            "WHERE grpm.member.id = :memberId " +
            "AND g.date >= :since " +
            "AND g.date <= :until")
    List<HomeSummaryData> findRecentGatheringMemberData(
            @Param("memberId") UUID memberId,
            @Param("since") LocalDate since,
            @Param("until") LocalDate until);
}