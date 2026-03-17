package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface DepartmentQueryUseCase {

    List<Department> getDepartmentsByChurchId(ChurchId churchId);

    Department getDepartmentById(DepartmentId departmentId);

    List<DepartmentMember> getDepartmentMembers(DepartmentId departmentId);

    List<DepartmentMember> getActiveDepartmentMembers(DepartmentId departmentId);

    List<DepartmentMember> getMyDepartments(MemberId memberId, ChurchId churchId);

    DepartmentRole getCurrentRole(MemberId memberId, DepartmentId departmentId);

    List<Member> getBirthdayMembers(DepartmentId departmentId, int month);

    boolean hasElevatedSearchAccess(MemberId memberId, DepartmentId departmentId);

    List<MemberWithGroups> searchDepartmentMembers(DepartmentId departmentId, String searchText);
}
