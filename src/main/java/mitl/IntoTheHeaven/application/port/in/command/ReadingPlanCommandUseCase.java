package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReadingPlanCommandUseCase {

    ReadingPlan createPlan(String title, LocalDate startDate, int totalDays);

    ReadingPlanDay createDay(UUID planId, LocalDate readingDate, int dayNumber,
                             String readingRange, String youtubeUrl, String description);

    void createDaysBatch(UUID planId, List<DayInput> days);

    void activatePlanForDepartment(DepartmentId departmentId, UUID planId, LocalDate startDate, LocalDate endDate);

    void deactivatePlanForDepartment(DepartmentId departmentId);

    record DayInput(LocalDate readingDate, int dayNumber, String readingRange,
                    String youtubeUrl, String description) {}
}
