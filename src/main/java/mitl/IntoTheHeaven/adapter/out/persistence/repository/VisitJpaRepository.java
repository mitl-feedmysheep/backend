package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VisitJpaRepository extends JpaRepository<VisitJpaEntity, UUID>, VisitJpaRepositoryCustom {

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
