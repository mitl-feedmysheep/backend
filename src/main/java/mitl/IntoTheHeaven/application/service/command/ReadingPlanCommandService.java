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
    public ReadingPlan createPlan(UUID churchId, String title, int readingDays) {
        ReadingPlan plan = ReadingPlan.builder()
                .id(ReadingPlanId.from(UUID.randomUUID()))
                .churchId(churchId)
                .title(title)
                .readingDays(readingDays)
                .build();
        return readingPlanPort.save(plan);
    }

    @Override
    public ReadingPlanDay createDay(UUID planId, int dayNumber, String readingRange,
                                    String audioUrl, String videoUrl, String description) {
        ReadingPlanDay day = ReadingPlanDay.builder()
                .id(ReadingPlanDayId.from(UUID.randomUUID()))
                .readingPlanId(ReadingPlanId.from(planId))
                .dayNumber(dayNumber)
                .readingRange(readingRange)
                .audioUrl(audioUrl)
                .videoUrl(videoUrl)
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
                        .dayNumber(d.dayNumber())
                        .readingRange(d.readingRange())
                        .audioUrl(d.audioUrl())
                        .videoUrl(d.videoUrl())
                        .description(d.description())
                        .build())
                .toList();
        readingPlanPort.saveDays(entities);
    }

    @Override
    public void activatePlanForDepartment(DepartmentId departmentId, UUID planId,
                                          LocalDate startDate, LocalDate endDate) {
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
