package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadingPlanQueryService implements ReadingPlanQueryUseCase {

    private final ReadingPlanPort readingPlanPort;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionHistoryJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;

    @Override
    public boolean isReadingEnabled(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .isPresent();
    }

    @Override
    public ReadingPlanDay getTodayReading(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .flatMap(mapping -> readingPlanPort.findDayByPlanIdAndDate(
                        mapping.getReadingPlan().getId(), LocalDate.now()))
                .orElse(null);
    }

    @Override
    public List<ReadingPlanDay> getAllDays(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .map(mapping -> readingPlanPort.findDaysByPlanId(mapping.getReadingPlan().getId()))
                .orElse(List.of());
    }

    @Override
    public MyReadingProgress getMyProgress(DepartmentId departmentId, MemberId memberId) {
        Optional<DepartmentReadingPlanJpaEntity> mappingOpt = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now());
        if (mappingOpt.isEmpty()) {
            return new MyReadingProgress(0, 0, 0, 0, List.of());
        }

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = mappingOpt.get().getReadingPlan().getTotalDays();

        var completions = readingCompletionHistoryJpaRepository
                .findByDepartmentReadingPlanIdAndMemberId(deptPlanId, memberId.getValue());

        List<LocalDate> completedDates = completions.stream()
                .map(rc -> rc.getCompletedAt().toLocalDate())
                .sorted()
                .toList();

        int percent = totalDays > 0 ? (int) Math.round((completions.size() * 100.0) / totalDays) : 0;
        int streak = calculateStreak(completedDates);

        return new MyReadingProgress(completions.size(), totalDays, percent, streak, completedDates);
    }

    @Override
    public List<MemberReadingProgress> getDepartmentProgress(DepartmentId departmentId) {
        Optional<DepartmentReadingPlanJpaEntity> mappingOpt = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now());
        if (mappingOpt.isEmpty()) return List.of();

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = mappingOpt.get().getReadingPlan().getTotalDays();

        return departmentMemberJpaRepository
                .findByDepartmentIdAndStatus(departmentId.getValue(), DepartmentMemberStatus.ACTIVE)
                .stream()
                .map(dm -> {
                    var member = dm.getMember();
                    long completed = readingCompletionHistoryJpaRepository
                            .countByDepartmentReadingPlanIdAndMemberId(deptPlanId, member.getId());
                    int percent = totalDays > 0 ? (int) Math.round((completed * 100.0) / totalDays) : 0;
                    return new MemberReadingProgress(member.getId(), member.getName(),
                            (int) completed, totalDays, percent);
                })
                .toList();
    }

    private int calculateStreak(List<LocalDate> sortedDates) {
        if (sortedDates.isEmpty()) return 0;
        Set<LocalDate> dateSet = new HashSet<>(sortedDates);
        LocalDate check = LocalDate.now();
        int streak = 0;
        while (dateSet.contains(check)) {
            streak++;
            check = check.minusDays(1);
        }
        return streak;
    }
}
