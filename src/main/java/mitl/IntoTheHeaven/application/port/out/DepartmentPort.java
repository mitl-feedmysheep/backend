package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.Member;

import mitl.IntoTheHeaven.application.dto.MemberWithGroups;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentPort {

    Department save(Department department);

    Optional<Department> findById(UUID departmentId);

    List<Department> findByChurchId(UUID churchId);

    void deleteById(UUID departmentId);

    DepartmentMember saveDepartmentMember(DepartmentMember departmentMember, UUID departmentId, UUID memberId);

    List<DepartmentMember> findDepartmentMembersByDepartmentId(UUID departmentId);

    List<DepartmentMember> findActiveDepartmentMembersByDepartmentId(UUID departmentId);

    Optional<DepartmentMember> findDepartmentMemberByDepartmentIdAndMemberId(UUID departmentId, UUID memberId);

    List<DepartmentMember> findDepartmentMembersByMemberId(UUID memberId);

    List<DepartmentMember> findDepartmentMembersByMemberIdAndChurchId(UUID memberId, UUID churchId);

    DepartmentMember updateDepartmentMemberRole(UUID departmentMemberId, DepartmentRole newRole);

    DepartmentMember updateDepartmentMemberStatus(UUID departmentMemberId, DepartmentMemberStatus newStatus);

    void deleteDepartmentMember(UUID departmentMemberId);

    long countActiveMembersByDepartmentId(UUID departmentId);

    long countGroupsByDepartmentId(UUID departmentId);

    List<Member> findBirthdayMembersByDepartmentIdAndMonth(UUID departmentId, int month);

    List<MemberWithGroups> findMembersByDepartmentIdAndSearch(UUID departmentId, String searchText);
}
