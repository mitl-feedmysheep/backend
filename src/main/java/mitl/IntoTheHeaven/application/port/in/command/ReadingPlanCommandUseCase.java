package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface ReadingPlanCommandUseCase {

    ReadingPlan createPlan(UUID churchId, String title, int readingDays);

    ReadingPlanDay createDay(UUID planId, int dayNumber, String readingRange,
                             String audioUrl, String videoUrl, String description);

    void createDaysBatch(UUID planId, List<DayInput> days);

    void activatePlanForDepartment(DepartmentId departmentId, UUID planId, LocalDate startDate, LocalDate endDate);

    void deactivatePlanForDepartment(DepartmentId departmentId);

    record DayInput(int dayNumber, String readingRange, String audioUrl, String videoUrl, String description) {}
}
