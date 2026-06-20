package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentReadingPlanJpaRepository extends JpaRepository<DepartmentReadingPlanJpaEntity, UUID> {

    /** 오늘 날짜가 운영 기간 내에 있는 활성 매핑 조회 */
    @Query("SELECT e FROM DepartmentReadingPlanJpaEntity e JOIN FETCH e.readingPlan " +
           "WHERE e.department.id = :departmentId AND :today BETWEEN e.startDate AND e.endDate")
    Optional<DepartmentReadingPlanJpaEntity> findActiveByDepartmentIdAndDate(
            @Param("departmentId") UUID departmentId, @Param("today") LocalDate today);

    /** 부서별 전체 매핑 이력 */
    List<DepartmentReadingPlanJpaEntity> findByDepartmentId(UUID departmentId);

    /** 오늘 기준 운영 중인 모든 매핑 (푸시 스케줄러용) */
    @Query("SELECT DISTINCT e FROM DepartmentReadingPlanJpaEntity e JOIN FETCH e.department JOIN FETCH e.readingPlan " +
           "WHERE :today BETWEEN e.startDate AND e.endDate")
    List<DepartmentReadingPlanJpaEntity> findAllActiveByDate(@Param("today") LocalDate today);
}
