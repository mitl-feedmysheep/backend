package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"groupMembers", "groupMembers.member"})
    List<GroupJpaEntity> findAllByChurchId(UUID churchId);

    @EntityGraph(attributePaths = {"groupMembers", "groupMembers.member"})
    List<GroupJpaEntity> findAllByChurchIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID churchId, LocalDate yearEnd, LocalDate yearStart);

    List<GroupJpaEntity> findAllByDepartmentId(UUID departmentId);
}
