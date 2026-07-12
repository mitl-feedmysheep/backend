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
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadingPlanQueryService implements ReadingPlanQueryUseCase {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ReadingPlanPort readingPlanPort;
    private final MediaPort mediaPort;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;
    private final ReadingCompletionHistoryJpaRepository readingCompletionHistoryJpaRepository;
    private final DepartmentMemberJpaRepository departmentMemberJpaRepository;

    @Override
    public String getActivePlanTitle(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .map(mapping -> mapping.getReadingPlan().getTitle())
                .orElse(null);
    }

    @Override
    public boolean isReadingEnabled(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .isPresent();
    }

    @Override
    public ReadingPlanDay getTodayReading(DepartmentId departmentId) {
        return departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .flatMap(mapping -> {
                    int readingDays = mapping.getReadingPlan().getReadingDays();
                    int dayNumber = computeDayNumber(mapping.getStartDate(), LocalDate.now(KST), readingDays);
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
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .map(mapping -> {
                    int dayNumber = computeDayNumber(
                            mapping.getStartDate(), LocalDate.now(KST),
                            mapping.getReadingPlan().getReadingDays());
                    if (dayNumber == 0) return 0;
                    return readingPlanPort.findDayByPlanIdAndDayNumber(
                                    mapping.getReadingPlan().getId(), dayNumber)
                            .map(day -> (int) readingCompletionHistoryJpaRepository
                                    .countDistinctMemberByDeptPlanIdAndDayId(
                                            mapping.getId(), day.getId().getValue()))
                            .orElse(0);
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
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .map(mapping -> readingPlanPort.findDaysByPlanId(mapping.getReadingPlan().getId()))
                .orElse(List.of());
    }

    @Override
    public MyReadingProgress getMyProgress(DepartmentId departmentId, MemberId memberId) {
        Optional<DepartmentReadingPlanJpaEntity> mappingOpt = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST));
        if (mappingOpt.isEmpty()) {
            return new MyReadingProgress(0, 0, 0, 0, List.of(), List.of());
        }

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = readingPlanPort.countDaysByPlanId(mappingOpt.get().getReadingPlan().getId());

        List<LocalDate> scheduledDates = computeScheduledDates(
                mappingOpt.get().getStartDate(),
                mappingOpt.get().getEndDate(),
                mappingOpt.get().getReadingPlan().getReadingDays(),
                totalDays
        );

        List<Integer> completedDayNums = readingCompletionHistoryJpaRepository
                .findCompletedDayNumbersByDeptPlanIdAndMemberId(deptPlanId, memberId.getValue());

        List<LocalDate> completedDates = completedDayNums.stream()
                .filter(dn -> dn >= 1 && dn <= scheduledDates.size())
                .map(dn -> scheduledDates.get(dn - 1))
                .sorted()
                .toList();

        int percent = totalDays > 0 ? (int) Math.round((completedDayNums.size() * 100.0) / totalDays) : 0;
        int streak = calculateStreak(completedDates);

        return new MyReadingProgress(completedDayNums.size(), totalDays, percent, streak, completedDates, scheduledDates);
    }

    @Override
    public List<MemberReadingProgress> getDepartmentProgress(DepartmentId departmentId) {
        Optional<DepartmentReadingPlanJpaEntity> mappingOpt = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST));
        if (mappingOpt.isEmpty()) return List.of();

        UUID deptPlanId = mappingOpt.get().getId();
        int totalDays = readingPlanPort.countDaysByPlanId(mappingOpt.get().getReadingPlan().getId());

        return departmentMemberJpaRepository
                .findByDepartmentIdAndStatus(departmentId.getValue(), DepartmentMemberStatus.ACTIVE)
                .stream()
                .map(dm -> {
                    var member = dm.getMember();
                    long completed = readingCompletionHistoryJpaRepository
                            .countByDepartmentReadingPlanIdAndMemberIdAndIsCompletedTrue(deptPlanId, member.getId());
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
        LocalDate limit = endDate.isBefore(LocalDate.now(KST)) ? endDate : LocalDate.now(KST);
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
        LocalDate check = LocalDate.now(KST);
        int streak = 0;
        while (dateSet.contains(check)) {
            streak++;
            check = check.minusDays(1);
        }
        return streak;
    }
}
