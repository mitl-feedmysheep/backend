package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingPlanPort {

    ReadingPlan save(ReadingPlan readingPlan);

    Optional<ReadingPlan> findById(UUID id);

    ReadingPlanDay saveDay(ReadingPlanDay day);

    List<ReadingPlanDay> saveDays(List<ReadingPlanDay> days);

    Optional<ReadingPlanDay> findDayById(UUID id);

    Optional<ReadingPlanDay> findDayByPlanIdAndDate(UUID planId, LocalDate date);

    List<ReadingPlanDay> findDaysByPlanId(UUID planId);

    DepartmentReadingPlan saveMapping(DepartmentReadingPlan mapping);

    Optional<DepartmentReadingPlan> findActiveMappingByDepartmentId(UUID departmentId);

    List<DepartmentReadingPlan> findAllActiveMappings();

    void deleteMapping(UUID mappingId);
}
