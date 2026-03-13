package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.DepartmentCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.DepartmentMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentCommandService implements DepartmentCommandUseCase {

    private final DepartmentPort departmentPort;

    @Override
    public Department createDepartment(ChurchId churchId, String name, String description) {
        Department department = Department.builder()
                .id(DepartmentId.from(java.util.UUID.randomUUID()))
                .churchId(churchId)
                .name(name)
                .description(description)
                .build();
        return departmentPort.save(department);
    }

    @Override
    public Department updateDepartment(DepartmentId departmentId, String name, String description) {
        Department existing = departmentPort.findById(departmentId.getValue())
                .orElseThrow(() -> new RuntimeException("Department not found: " + departmentId.getValue()));

        Department updated = Department.builder()
                .id(existing.getId())
                .churchId(existing.getChurchId())
                .name(name != null ? name : existing.getName())
                .description(description != null ? description : existing.getDescription())
                .build();
        return departmentPort.save(updated);
    }

    @Override
    public void deleteDepartment(DepartmentId departmentId) {
        Department department = departmentPort.findById(departmentId.getValue())
                .orElseThrow(() -> new RuntimeException("Department not found: " + departmentId.getValue()));

        if (department.isDefault()) {
            throw new IllegalStateException("기본 부서는 삭제할 수 없습니다.");
        }

        long memberCount = departmentPort.countActiveMembersByDepartmentId(departmentId.getValue());
        if (memberCount > 0) {
            throw new IllegalStateException("부서에 활동 중인 멤버가 있어 삭제할 수 없습니다.");
        }

        long groupCount = departmentPort.countGroupsByDepartmentId(departmentId.getValue());
        if (groupCount > 0) {
            throw new IllegalStateException("부서에 소속된 그룹이 있어 삭제할 수 없습니다.");
        }

        departmentPort.deleteById(departmentId.getValue());
    }

    @Override
    public DepartmentMember addMember(DepartmentId departmentId, MemberId memberId, DepartmentRole role) {
        // 중복 확인
        departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(
                departmentId.getValue(), memberId.getValue())
                .ifPresent(dm -> {
                    throw new IllegalStateException("이미 부서에 소속된 멤버입니다.");
                });

        DepartmentMember departmentMember = DepartmentMember.builder()
                .id(DepartmentMemberId.from(java.util.UUID.randomUUID()))
                .departmentId(departmentId)
                .role(role)
                .status(DepartmentMemberStatus.ACTIVE)
                .build();

        return departmentPort.saveDepartmentMember(departmentMember, departmentId.getValue(), memberId.getValue());
    }

    @Override
    public void removeMember(DepartmentId departmentId, MemberId memberId) {
        DepartmentMember dm = departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(
                        departmentId.getValue(), memberId.getValue())
                .orElseThrow(() -> new RuntimeException("부서 멤버를 찾을 수 없습니다."));
        departmentPort.deleteDepartmentMember(dm.getId().getValue());
    }

    @Override
    public DepartmentMember changeMemberRole(DepartmentId departmentId, MemberId memberId, DepartmentRole newRole) {
        DepartmentMember dm = departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(
                        departmentId.getValue(), memberId.getValue())
                .orElseThrow(() -> new RuntimeException("부서 멤버를 찾을 수 없습니다."));
        return departmentPort.updateDepartmentMemberRole(dm.getId().getValue(), newRole);
    }

    @Override
    public DepartmentMember changeMemberStatus(DepartmentId departmentId, MemberId memberId, DepartmentMemberStatus newStatus) {
        DepartmentMember dm = departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(
                        departmentId.getValue(), memberId.getValue())
                .orElseThrow(() -> new RuntimeException("부서 멤버를 찾을 수 없습니다."));
        return departmentPort.updateDepartmentMemberStatus(dm.getId().getValue(), newStatus);
    }
}
