package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.ReadingPlanCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.ReadingPlanPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadingPlanCommandService implements ReadingPlanCommandUseCase {

    private final ReadingPlanPort readingPlanPort;

    @Override
    public ReadingPlan createPlan(String title, LocalDate startDate, int totalDays) {
        ReadingPlan plan = ReadingPlan.builder()
                .id(ReadingPlanId.from(UUID.randomUUID()))
                .title(title)
                .startDate(startDate)
                .totalDays(totalDays)
                .build();
        return readingPlanPort.save(plan);
    }

    @Override
    public ReadingPlanDay createDay(UUID planId, LocalDate readingDate, int dayNumber,
                                   String readingRange, String youtubeUrl, String description) {
        ReadingPlanDay day = ReadingPlanDay.builder()
                .id(ReadingPlanDayId.from(UUID.randomUUID()))
                .readingPlanId(ReadingPlanId.from(planId))
                .readingDate(readingDate)
                .dayNumber(dayNumber)
                .readingRange(readingRange)
                .youtubeUrl(youtubeUrl)
                .description(description)
                .build();
        return readingPlanPort.saveDay(day);
    }

    @Override
    public void createDaysBatch(UUID planId, List<DayInput> days) {
        List<ReadingPlanDay> entities = days.stream()
                .map(d -> (ReadingPlanDay) ReadingPlanDay.builder()
                        .id(ReadingPlanDayId.from(UUID.randomUUID()))
                        .readingPlanId(ReadingPlanId.from(planId))
                        .readingDate(d.readingDate())
                        .dayNumber(d.dayNumber())
                        .readingRange(d.readingRange())
                        .youtubeUrl(d.youtubeUrl())
                        .description(d.description())
                        .build())
                .toList();
        readingPlanPort.saveDays(entities);
    }

    @Override
    public void activatePlanForDepartment(DepartmentId departmentId, UUID planId,
                                          LocalDate startDate, LocalDate endDate) {
        // 기존 운영 중인 매핑 soft delete
        readingPlanPort.findActiveMappingByDepartmentId(departmentId.getValue())
                .ifPresent(existing -> readingPlanPort.saveMapping(existing.delete()));

        DepartmentReadingPlan mapping = DepartmentReadingPlan.builder()
                .id(DepartmentReadingPlanId.from(UUID.randomUUID()))
                .departmentId(departmentId)
                .readingPlanId(ReadingPlanId.from(planId))
                .startDate(startDate)
                .endDate(endDate)
                .build();
        readingPlanPort.saveMapping(mapping);
    }

    @Override
    public void deactivatePlanForDepartment(DepartmentId departmentId) {
        readingPlanPort.findActiveMappingByDepartmentId(departmentId.getValue())
                .ifPresent(existing -> readingPlanPort.saveMapping(existing.delete()));
    }
}
