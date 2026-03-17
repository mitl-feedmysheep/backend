package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentMemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentMemberJpaRepository extends JpaRepository<DepartmentMemberJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"member"})
    List<DepartmentMemberJpaEntity> findByDepartmentIdAndStatus(UUID departmentId, DepartmentMemberStatus status);

    @EntityGraph(attributePaths = {"member"})
    List<DepartmentMemberJpaEntity> findByDepartmentId(UUID departmentId);

    Optional<DepartmentMemberJpaEntity> findByDepartment_IdAndMember_Id(UUID departmentId, UUID memberId);

    @EntityGraph(attributePaths = {"department"})
    List<DepartmentMemberJpaEntity> findByMember_Id(UUID memberId);

    @EntityGraph(attributePaths = {"department", "department.church"})
    List<DepartmentMemberJpaEntity> findByMember_IdAndDepartment_Church_Id(UUID memberId, UUID churchId);
}
