package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface DepartmentCommandUseCase {

    Department createDepartment(ChurchId churchId, String name, String description);

    Department updateDepartment(DepartmentId departmentId, String name, String description);

    void deleteDepartment(DepartmentId departmentId);

    DepartmentMember addMember(DepartmentId departmentId, MemberId memberId, DepartmentRole role);

    void removeMember(DepartmentId departmentId, MemberId memberId);

    DepartmentMember changeMemberRole(DepartmentId departmentId, MemberId memberId, DepartmentRole newRole);

    DepartmentMember changeMemberStatus(DepartmentId departmentId, MemberId memberId, DepartmentMemberStatus newStatus);
}
