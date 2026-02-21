package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberRequestJpaEntity;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChurchMemberRequestJpaRepository extends JpaRepository<ChurchMemberRequestJpaEntity, UUID> {

    Optional<ChurchMemberRequestJpaEntity> findByMemberIdAndChurchIdAndStatus(
            UUID memberId, UUID churchId, RequestStatus status);

    @EntityGraph(attributePaths = {"church"})
    List<ChurchMemberRequestJpaEntity> findAllByMemberId(UUID memberId);
}
