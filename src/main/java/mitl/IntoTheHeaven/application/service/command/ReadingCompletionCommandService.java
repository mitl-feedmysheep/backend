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
import java.time.ZoneId;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadingCompletionCommandService implements ReadingCompletionCommandUseCase {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ReadingCompletionHistoryPort readingCompletionHistoryPort;
    private final ReadingPlanPort readingPlanPort;
    private final DepartmentReadingPlanJpaRepository departmentReadingPlanJpaRepository;

    @Override
    public void markComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId) {
        var mapping = departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .orElseThrow(() -> new RuntimeException("No active reading plan for department"));

        UUID deptPlanId = mapping.getId();

        var existing = readingCompletionHistoryPort
                .findByDeptPlanIdAndDayIdAndMemberId(deptPlanId, dayId.getValue(), memberId.getValue());

        if (existing.isPresent()) {
            if (existing.get().isCompleted()) return; // 이미 완독
            // 취소 상태였으면 다시 완독으로 전환
            readingCompletionHistoryPort.setIsCompleted(deptPlanId, dayId.getValue(), memberId.getValue(), true);
            return;
        }

        readingPlanPort.findDayById(dayId.getValue())
                .orElseThrow(() -> new RuntimeException("Reading plan day not found"));

        ReadingCompletionHistory history = ReadingCompletionHistory.builder()
                .id(ReadingCompletionHistoryId.from(UUID.randomUUID()))
                .departmentReadingPlanId(DepartmentReadingPlanId.from(deptPlanId))
                .readingPlanDayId(dayId)
                .memberId(memberId)
                .completedAt(LocalDateTime.now(KST))
                .isCompleted(true)
                .build();
        readingCompletionHistoryPort.save(history);
    }

    @Override
    public void unmarkComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId) {
        departmentReadingPlanJpaRepository
                .findActiveByDepartmentIdAndDate(departmentId.getValue(), LocalDate.now(KST))
                .ifPresent(mapping -> readingCompletionHistoryPort.setIsCompleted(
                        mapping.getId(), dayId.getValue(), memberId.getValue(), false));
    }
}
