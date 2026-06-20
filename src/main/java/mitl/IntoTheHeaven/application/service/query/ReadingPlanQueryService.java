package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentReadingPlanJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReadingCompletionHistoryJpaRepository;
import mitl.IntoTheHeaven.application.port.in.query.ReadingPlanQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadingPlanQueryService implements ReadingPlanQueryUseCase {

    private final ReadingPlanPort readingPlanPort;
    private final MediaPort mediaPort;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionHistoryJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;

    @Override
    public String getActivePlanTitle(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .map(mapping -> mapping.getReadingPlan().getTitle())
                .orElse(null);
    }

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
                .flatMap(mapping -> {
                    int readingDays = mapping.getReadingPlan().getReadingDays();
                    int dayNumber = computeDayNumber(mapping.getStartDate(), LocalDate.now(), readingDays);
                    if (dayNumber == 0) return Optional.empty();
                    return readingPlanPort.findDayByPlanIdAndDayNumber(
                            mapping.getReadingPlan().getId(), dayNumber);
                })
                .map(day -> {
                    List<Media> medias = mediaPort.findByEntity(EntityType.READING_DAY, day.getId().getValue());
                    return day.toBuilder().medias(medias).build();
                })
                .orElse(null);
    }

    @Override
    public int getTodayCompletionCount(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .map(mapping -> {
                    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                    LocalDateTime endOfDay = startOfDay.plusDays(1);
                    return (int) readingCompletionHistoryJpaRepository
                            .countDistinctMemberByDeptPlanIdAndDate(mapping.getId(), startOfDay, endOfDay);
                })
                .orElse(0);
    }

    @Override
    public ReadingPlanDay getReadingByDate(DepartmentId departmentId, LocalDate date) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), date)
                .flatMap(mapping -> {
                    int readingDays = mapping.getReadingPlan().getReadingDays();
                    int dayNumber = computeDayNumber(mapping.getStartDate(), date, readingDays);
                    if (dayNumber == 0) return Optional.empty();
                    return readingPlanPort.findDayByPlanIdAndDayNumber(
                            mapping.getReadingPlan().getId(), dayNumber);
                })
                .map(day -> {
                    List<Media> medias = mediaPort.findByEntity(EntityType.READING_DAY, day.getId().getValue());
                    return day.toBuilder().medias(medias).build();
                })
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
            return new MyReadingProgress(0, 0, 0, 0, List.of(), List.of());
        }

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = readingPlanPort.countDaysByPlanId(mappingOpt.get().getReadingPlan().getId());

        var completions = readingCompletionHistoryJpaRepository
                .findByDepartmentReadingPlanIdAndMemberId(deptPlanId, memberId.getValue());

        List<LocalDate> completedDates = completions.stream()
                .map(rc -> rc.getCompletedAt().toLocalDate())
                .sorted()
                .toList();

        int percent = totalDays > 0 ? (int) Math.round((completions.size() * 100.0) / totalDays) : 0;
        int streak = calculateStreak(completedDates);

        List<LocalDate> scheduledDates = computeScheduledDates(
                mappingOpt.get().getStartDate(),
                mappingOpt.get().getEndDate(),
                mappingOpt.get().getReadingPlan().getReadingDays(),
                totalDays
        );

        return new MyReadingProgress(completions.size(), totalDays, percent, streak, completedDates, scheduledDates);
    }

    @Override
    public List<MemberReadingProgress> getDepartmentProgress(DepartmentId departmentId) {
        Optional<DepartmentReadingPlanJpaEntity> mappingOpt = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now());
        if (mappingOpt.isEmpty()) return List.of();

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = readingPlanPort.countDaysByPlanId(mappingOpt.get().getReadingPlan().getId());

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

    /**
     * dept_reading_plan.start_date 기준으로 today가 몇 일차인지 계산.
     * readingDaysMask: 비트마스크 (bit0=월, bit1=화, ..., bit6=일).
     * today가 읽기 요일이 아니거나 start_date 이전이면 0 반환.
     */
    public static int computeDayNumber(LocalDate startDate, LocalDate today, int readingDaysMask) {
        if (today.isBefore(startDate)) return 0;
        int todayBit = 1 << (today.getDayOfWeek().getValue() - 1);
        if ((readingDaysMask & todayBit) == 0) return 0;
        int count = 0;
        LocalDate d = startDate;
        while (!d.isAfter(today)) {
            int dayBit = 1 << (d.getDayOfWeek().getValue() - 1);
            if ((readingDaysMask & dayBit) != 0) count++;
            d = d.plusDays(1);
        }
        return count;
    }

    private List<LocalDate> computeScheduledDates(LocalDate startDate, LocalDate endDate,
                                                   int readingDaysMask, int totalDays) {
        LocalDate limit = endDate.isBefore(LocalDate.now()) ? endDate : LocalDate.now();
        List<LocalDate> scheduled = new ArrayList<>();
        LocalDate d = startDate;
        while (!d.isAfter(limit) && scheduled.size() < totalDays) {
            int dayBit = 1 << (d.getDayOfWeek().getValue() - 1);
            if ((readingDaysMask & dayBit) != 0) scheduled.add(d);
            d = d.plusDays(1);
        }
        return scheduled;
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
