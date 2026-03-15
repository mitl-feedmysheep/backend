package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.DepartmentQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentQueryService implements DepartmentQueryUseCase {

    private final DepartmentPort departmentPort;

    @Override
    public List<Department> getDepartmentsByChurchId(ChurchId churchId) {
        return departmentPort.findByChurchId(churchId.getValue());
    }

    @Override
    public Department getDepartmentById(DepartmentId departmentId) {
        return departmentPort.findById(departmentId.getValue())
                .orElseThrow(() -> new RuntimeException("Department not found: " + departmentId.getValue()));
    }

    @Override
    public List<DepartmentMember> getDepartmentMembers(DepartmentId departmentId) {
        return departmentPort.findDepartmentMembersByDepartmentId(departmentId.getValue());
    }

    @Override
    public List<DepartmentMember> getActiveDepartmentMembers(DepartmentId departmentId) {
        return departmentPort.findActiveDepartmentMembersByDepartmentId(departmentId.getValue());
    }

    @Override
    public List<DepartmentMember> getMyDepartments(MemberId memberId, ChurchId churchId) {
        return departmentPort.findDepartmentMembersByMemberIdAndChurchId(memberId.getValue(), churchId.getValue());
    }

    @Override
    public DepartmentRole getCurrentRole(MemberId memberId, DepartmentId departmentId) {
        return departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(
                        departmentId.getValue(), memberId.getValue())
                .map(DepartmentMember::getRole)
                .orElse(null);
    }

    @Override
    public List<Member> getBirthdayMembers(DepartmentId departmentId, int month) {
        return departmentPort.findBirthdayMembersByDepartmentIdAndMonth(departmentId.getValue(), month);
    }

    @Override
    public boolean hasElevatedSearchAccess(MemberId memberId, DepartmentId departmentId) {
        DepartmentRole role = getCurrentRole(memberId, departmentId);
        if (role == null) {
            throw new IllegalArgumentException("해당 부서의 멤버가 아닙니다.");
        }
        return role == DepartmentRole.LEADER || role == DepartmentRole.ADMIN;
    }

    @Override
    public List<MemberWithGroups> searchDepartmentMembers(DepartmentId departmentId, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }
        return departmentPort.findMembersByDepartmentIdAndSearch(departmentId.getValue(), searchText.trim());
    }
}
