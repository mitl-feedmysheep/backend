package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.ReadingPlanDayId;

public interface ReadingCompletionCommandUseCase {

    void markComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId);

    void unmarkComplete(DepartmentId departmentId, ReadingPlanDayId dayId, MemberId memberId);
}
