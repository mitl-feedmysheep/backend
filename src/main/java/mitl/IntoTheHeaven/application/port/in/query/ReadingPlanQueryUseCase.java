package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReadingPlanQueryUseCase {

    /**
     * 오늘 분량 조회 (없으면 null — 플랜이 없거나 비활성 부서)
     */
    ReadingPlanDay getTodayReading(DepartmentId departmentId);

    /**
     * 현재 활성 플랜 제목 (없으면 null)
     */
    String getActivePlanTitle(DepartmentId departmentId);

    /**
     * 부서 활성 플랜 여부
     */
    boolean isReadingEnabled(DepartmentId departmentId);

    /**
     * 특정 날짜 분량 조회 (없으면 null — 읽기 요일이 아닌 날, 플랜 기간 밖)
     */
    ReadingPlanDay getReadingByDate(DepartmentId departmentId, LocalDate date);

    /**
     * 전체 일자 목록 (캘린더/지난 날 보충용)
     */
    List<ReadingPlanDay> getAllDays(DepartmentId departmentId);

    /**
     * 오늘 부서 완독 인원 수
     */
    int getTodayCompletionCount(DepartmentId departmentId);

    /**
     * 내 진도 요약
     */
    MyReadingProgress getMyProgress(DepartmentId departmentId, MemberId memberId);

    /**
     * 부서 전체 멤버 진도 목록 (리더/관리자)
     */
    List<MemberReadingProgress> getDepartmentProgress(DepartmentId departmentId);

    record MyReadingProgress(int completedCount, int totalDays, int progressPercent,
                              int streak, java.util.List<java.time.LocalDate> completedDates,
                              java.util.List<java.time.LocalDate> scheduledDates) {}

    record MemberReadingProgress(UUID memberId, String memberName, int completedCount,
                                 int totalDays, int progressPercent) {}
}
