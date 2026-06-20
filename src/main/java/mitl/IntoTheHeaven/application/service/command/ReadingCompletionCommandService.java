package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.DepartmentReadingPlanJpaRepository;
import mitl.IntoTheHeaven.application.port.in.command.ReadingCompletionCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.ReadingCompletionHistoryPort;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadingCompletionCommandService implements ReadingCompletionCommandUseCase {

    private final ReadingCompletionHistoryPort readingCompletionHistoryPort;
    private final ReadingPlanPort readingPlanPort;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;

    @Override
    public void markComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId) {
        var mapping = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No active reading plan for department"));

        UUID deptPlanId = mapping.getId();

        if (readingCompletionHistoryPort.existsByDeptPlanIdAndDayIdAndMemberId(
                deptPlanId, dayId.getValue(), memberId.getValue())) {
            return;
        }

        var day = readingPlanPort.findDayById(dayId.getValue())
                .orElseThrow(() -> new RuntimeException("Reading plan day not found"));

        // dayNumber로 실제 예정일 역산 — completed_at을 오늘이 아닌 해당 읽기 날짜로 저장
        LocalDate scheduledDate = computeScheduledDate(
                mapping.getStartDate(),
                mapping.getReadingPlan().getReadingDays(),
                day.getDayNumber()
        );

        ReadingCompletionHistory history = ReadingCompletionHistory.builder()
                .id(ReadingCompletionHistoryId.from(UUID.randomUUID()))
                .departmentReadingPlanId(DepartmentReadingPlanId.from(deptPlanId))
                .readingPlanDayId(dayId)
                .memberId(memberId)
                .completedAt(scheduledDate.atStartOfDay())
                .build();
        readingCompletionHistoryPort.save(history);
    }

    private LocalDate computeScheduledDate(LocalDate startDate, int readingDaysMask, int dayNumber) {
        LocalDate d = startDate;
        int count = 0;
        while (true) {
            int dayBit = 1 << (d.getDayOfWeek().getValue() - 1);
            if ((readingDaysMask & dayBit) != 0 && ++count == dayNumber) return d;
            d = d.plusDays(1);
        }
    }

    @Override
    public void unmarkComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId) {
        departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now())
                .ifPresent(mapping -> readingCompletionHistoryPort.deleteByDeptPlanIdAndDayIdAndMemberId(
                        mapping.getId(), dayId.getValue(), memberId.getValue()));
    }
}
