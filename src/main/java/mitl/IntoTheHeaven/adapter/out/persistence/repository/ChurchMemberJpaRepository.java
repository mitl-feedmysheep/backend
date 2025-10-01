package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;

public interface ChurchMemberJpaRepository extends JpaRepository<ChurchMemberJpaEntity, UUID> {
    List<ChurchMemberJpaEntity> findAllByMemberId(UUID memberId);

    List<ChurchMemberJpaEntity> findAllByChurchId(UUID churchId);

    /* ADMIN */
    ChurchMemberJpaEntity findByMemberIdAndChurchId(UUID memberId, UUID churchId);

    List<ChurchMemberJpaEntity> findAllByMemberIdAndRole(UUID memberId, ChurchRole role);
}
