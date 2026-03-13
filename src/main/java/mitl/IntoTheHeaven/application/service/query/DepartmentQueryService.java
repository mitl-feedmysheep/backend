package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.DepartmentQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
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
}
