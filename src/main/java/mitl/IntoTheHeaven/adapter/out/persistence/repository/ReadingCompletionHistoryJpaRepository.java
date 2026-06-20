package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReadingCompletionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingCompletionHistoryJpaRepository extends JpaRepository<ReadingCompletionHistoryJpaEntity, UUID> {

    Optional<ReadingCompletionHistoryJpaEntity> findByDepartmentReadingPlanIdAndReadingPlanDayIdAndMemberId(
            UUID deptPlanId, UUID dayId, UUID memberId);

    List<ReadingCompletionHistoryJpaEntity> findByDepartmentReadingPlanIdAndMemberIdAndIsCompletedTrue(
            UUID deptPlanId, UUID memberId);

    long countByDepartmentReadingPlanIdAndMemberIdAndIsCompletedTrue(UUID deptPlanId, UUID memberId);

    @Modifying
    @Query("UPDATE ReadingCompletionHistoryJpaEntity rc SET rc.isCompleted = :isCompleted " +
           "WHERE rc.departmentReadingPlan.id = :deptPlanId " +
           "AND rc.readingPlanDay.id = :dayId AND rc.member.id = :memberId")
    void updateIsCompleted(@Param("deptPlanId") UUID deptPlanId,
                           @Param("dayId") UUID dayId,
                           @Param("memberId") UUID memberId,
                           @Param("isCompleted") boolean isCompleted);

    /** is_completed=true인 day의 dayNumber 목록 — completedDates 역산용 */
    @Query("SELECT rc.readingPlanDay.dayNumber FROM ReadingCompletionHistoryJpaEntity rc " +
           "WHERE rc.departmentReadingPlan.id = :deptPlanId " +
           "AND rc.member.id = :memberId AND rc.isCompleted = true")
    List<Integer> findCompletedDayNumbersByDeptPlanIdAndMemberId(
            @Param("deptPlanId") UUID deptPlanId, @Param("memberId") UUID memberId);

    /** 특정 날짜에 완독(is_completed=true)한 멤버 ID 목록 (푸시 미완독 필터용) */
    @Query("SELECT rc.member.id FROM ReadingCompletionHistoryJpaEntity rc " +
           "WHERE rc.departmentReadingPlan.id = :deptPlanId AND rc.isCompleted = true " +
           "AND rc.completedAt >= :startOfDay AND rc.completedAt < :endOfDay")
    List<UUID> findMemberIdsByDeptPlanIdAndDate(@Param("deptPlanId") UUID deptPlanId,
                                                @Param("startOfDay") LocalDateTime startOfDay,
                                                @Param("endOfDay") LocalDateTime endOfDay);

    /** 특정 day를 완독(is_completed=true)한 고유 멤버 수 */
    @Query("SELECT COUNT(DISTINCT rc.member.id) FROM ReadingCompletionHistoryJpaEntity rc " +
           "WHERE rc.departmentReadingPlan.id = :deptPlanId AND rc.readingPlanDay.id = :dayId " +
           "AND rc.isCompleted = true")
    long countDistinctMemberByDeptPlanIdAndDayId(@Param("deptPlanId") UUID deptPlanId,
                                                 @Param("dayId") UUID dayId);
}
