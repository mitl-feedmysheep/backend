package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.ReadingCompletionHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingCompletionHistoryPort {

    ReadingCompletionHistory save(ReadingCompletionHistory history);

    Optional<ReadingCompletionHistory> findByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId);

    boolean existsByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId);

    void deleteByDeptPlanIdAndDayIdAndMemberId(UUID deptPlanId, UUID dayId, UUID memberId);

    long countByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId);

    List<ReadingCompletionHistory> findByDeptPlanIdAndMemberId(UUID deptPlanId, UUID memberId);

    /** 특정 날짜에 이미 완독한 멤버 ID 목록 (푸시 미완독 필터) */
    List<UUID> findCompletedMemberIdsByDeptPlanIdAndDate(UUID deptPlanId, LocalDate date);
}
