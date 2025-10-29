package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VisitJpaRepository extends JpaRepository<VisitJpaEntity, UUID>, VisitJpaRepositoryCustom {

    /**
     * Find visit by ID with full details (visitMembers, churchMember, prayers)
     * Note: medias는 @BatchSize로 처리 (multiple bag fetch exception 방지)
     */
    @EntityGraph(attributePaths = {
            "visitMembers",
            "visitMembers.churchMember",
            "visitMembers.churchMember.member",
    })
    Optional<VisitJpaEntity> findWithDetailsById(UUID id);
}
