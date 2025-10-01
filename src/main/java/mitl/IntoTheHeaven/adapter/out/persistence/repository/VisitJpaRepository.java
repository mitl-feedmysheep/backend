package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitJpaRepository extends JpaRepository<VisitJpaEntity, UUID>, VisitJpaRepositoryCustom {

    /**
     * Find all visits by church ID with basic info (ordered by date desc)
     */
    List<VisitJpaEntity> findAllByChurchIdOrderByDateDescStartedAtDesc(UUID churchId);

    /**
     * Find visit by ID with full details (visitMembers, churchMember, prayers)
     */
    @EntityGraph(attributePaths = {
            "visitMembers",
            "visitMembers.churchMember",
            "visitMembers.churchMember.member",
            "visitMembers.prayers"
    })
    Optional<VisitJpaEntity> findWithDetailsById(UUID id);
}

