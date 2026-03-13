package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface ChurchCommandUseCase {

    ChurchMemberRequest createJoinRequest(MemberId memberId, ChurchId churchId, DepartmentId departmentId);
}
