package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingCompletionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingCompletionHistoryJpaRepository extends JpaRepository<ReadingCompletionHistoryJpaEntity, UUID> {

    Optional<ReadingCompletionHistoryJpaEntity> findByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(
            UUID deptPlanId, UUID dayId, UUID memberId);

    boolean existsByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(
            UUID deptPlanId, UUID dayId, UUID memberId);

    List<ReadingCompletionHistoryJpaEntity> findByDepartmentReadingPlanIdAndMemberId(
            UUID deptPlanId, UUID memberId);

    long countByDepartmentReadingPlanIdAndMemberId(UUID deptPlanId, UUID memberId);

    /** 특정 날짜에 완독한 멤버 ID 목록 (푸시 미완독 필터용) */
    @Query("SELECT rc.member.id FROM ReadingCompletionHistoryJpaEntity rc " +
           "WHERE rc.departmentReadingPlan.id = :deptPlanId AND rc.readingPlanDay.readingDate = :date")
    List<UUID> findMemberIdsByDeptPlanIdAndDate(@Param("deptPlanId") UUID deptPlanId,
                                                @Param("date") LocalDate date);
}
