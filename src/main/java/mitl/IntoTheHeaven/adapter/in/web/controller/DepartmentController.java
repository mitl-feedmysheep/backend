package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.BirthdayMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.MemberSearchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.AddDepartmentMemberRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.ChangeDepartmentMemberRoleRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.CreateDepartmentRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.DepartmentMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.DepartmentResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.UpdateDepartmentRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupWithLeaderResponse;
import mitl.IntoTheHeaven.application.dto.GroupWithLeader;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.application.port.in.command.DepartmentCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.DepartmentQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.aop.RequireChurchRole;
import mitl.IntoTheHeaven.global.aop.RequireDepartmentRole;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Department", description = "APIs for Department Management")
@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentQueryUseCase departmentQueryUseCase;
    private final DepartmentCommandUseCase departmentCommandUseCase;
    private final GroupQueryUseCase groupQueryUseCase;

    @Operation(summary = "Get departments by church", description = "부서 목록 조회 (가입 시에도 사용)")
    @GetMapping("/churches/{churchId}/departments")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByChurch(
            @PathVariable("churchId") UUID churchId) {
        List<Department> departments = departmentQueryUseCase.getDepartmentsByChurchId(ChurchId.from(churchId));
        return ResponseEntity.ok(DepartmentResponse.from(departments));
    }

    @Operation(summary = "Create department", description = "부서 생성 (church ADMIN 이상)")
    @PostMapping("/churches/{churchId}/departments")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<DepartmentResponse> createDepartment(
            @PathVariable("churchId") UUID churchId,
            @Valid @RequestBody CreateDepartmentRequest request) {
        Department department = departmentCommandUseCase.createDepartment(
                ChurchId.from(churchId), request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.from(department));
    }

    @Operation(summary = "Update department", description = "부서 수정 (dept ADMIN 이상)")
    @PatchMapping("/departments/{departmentId}")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable("departmentId") UUID departmentId,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        Department department = departmentCommandUseCase.updateDepartment(
                DepartmentId.from(departmentId), request.getName(), request.getDescription());
        return ResponseEntity.ok(DepartmentResponse.from(department));
    }

    @Operation(summary = "Delete department", description = "부서 삭제 (church SUPER_ADMIN, 멤버/그룹 0일 때만)")
    @DeleteMapping("/departments/{departmentId}")
    @RequireChurchRole(ChurchRole.SUPER_ADMIN)
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable("departmentId") UUID departmentId) {
        departmentCommandUseCase.deleteDepartment(DepartmentId.from(departmentId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get department members", description = "부서 멤버 목록 (dept LEADER 이상)")
    @GetMapping("/departments/{departmentId}/members")
    @RequireDepartmentRole(DepartmentRole.LEADER)
    public ResponseEntity<List<DepartmentMemberResponse>> getDepartmentMembers(
            @PathVariable("departmentId") UUID departmentId) {
        List<DepartmentMember> members = departmentQueryUseCase.getActiveDepartmentMembers(DepartmentId.from(departmentId));
        return ResponseEntity.ok(DepartmentMemberResponse.from(members));
    }

    @Operation(summary = "Add member to department", description = "부서에 멤버 추가 (dept ADMIN 이상)")
    @PostMapping("/departments/{departmentId}/members")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<DepartmentMemberResponse> addMember(
            @PathVariable("departmentId") UUID departmentId,
            @Valid @RequestBody AddDepartmentMemberRequest request) {
        DepartmentMember dm = departmentCommandUseCase.addMember(
                DepartmentId.from(departmentId),
                MemberId.from(request.getMemberId()),
                request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentMemberResponse.from(dm));
    }

    @Operation(summary = "Remove member from department", description = "부서에서 멤버 제거 (dept ADMIN 이상)")
    @DeleteMapping("/departments/{departmentId}/members/{memberId}")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<Void> removeMember(
            @PathVariable("departmentId") UUID departmentId,
            @PathVariable("memberId") UUID memberId) {
        departmentCommandUseCase.removeMember(DepartmentId.from(departmentId), MemberId.from(memberId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change member role in department", description = "부서 멤버 역할 변경 (dept ADMIN 이상)")
    @PatchMapping("/departments/{departmentId}/members/{memberId}/role")
    @RequireDepartmentRole(DepartmentRole.ADMIN)
    public ResponseEntity<DepartmentMemberResponse> changeMemberRole(
            @PathVariable("departmentId") UUID departmentId,
            @PathVariable("memberId") UUID memberId,
            @Valid @RequestBody ChangeDepartmentMemberRoleRequest request) {
        DepartmentMember dm = departmentCommandUseCase.changeMemberRole(
                DepartmentId.from(departmentId), MemberId.from(memberId), request.getRole());
        return ResponseEntity.ok(DepartmentMemberResponse.from(dm));
    }

    @Operation(summary = "Get my groups in department", description = "부서 내 내가 속한 소그룹 목록")
    @GetMapping("/departments/{departmentId}/groups")
    public ResponseEntity<List<GroupResponse>> getGroupsInDepartment(
            @PathVariable("departmentId") UUID departmentId,
            @AuthenticationPrincipal String memberId) {
        List<Group> groups = groupQueryUseCase.getGroupsByMemberIdAndDepartmentId(
                MemberId.from(UUID.fromString(memberId)),
                DepartmentId.from(departmentId));
        List<GroupResponse> response = groups.stream()
                .map(g -> GroupResponse.from(g,
                        groupQueryUseCase.getGroupMembersByGroupId(g.getId().getValue())
                                .size()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get groups with leaders in department", description = "부서 내 전체 소그룹 + 리더 정보")
    @GetMapping("/departments/{departmentId}/groups-with-leaders")
    public ResponseEntity<List<GroupWithLeaderResponse>> getGroupsWithLeadersInDepartment(
            @PathVariable("departmentId") UUID departmentId) {
        List<GroupWithLeader> groups = groupQueryUseCase.getGroupsWithLeaderByDepartmentId(
                DepartmentId.from(departmentId));
        return ResponseEntity.ok(GroupWithLeaderResponse.from(groups));
    }

    @Operation(summary = "Search members in department", description = "부서 내 멤버 검색 (교적부)")
    @GetMapping("/departments/{departmentId}/members/search")
    public ResponseEntity<List<MemberSearchResponse>> searchMembersInDepartment(
            @PathVariable("departmentId") UUID departmentId,
            @RequestParam("searchText") String searchText,
            @AuthenticationPrincipal String memberId) {
        MemberId requesterId = MemberId.from(UUID.fromString(memberId));
        DepartmentId deptId = DepartmentId.from(departmentId);

        boolean isLeader = departmentQueryUseCase.hasElevatedSearchAccess(requesterId, deptId);

        List<MemberWithGroups> members = departmentQueryUseCase.searchDepartmentMembers(deptId, searchText);
        List<MemberSearchResponse> response = MemberSearchResponse.from(members, isLeader);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get birthday members in department", description = "부서 내 해당 월 생일자 조회")
    @GetMapping("/departments/{departmentId}/birthday-members")
    public ResponseEntity<List<BirthdayMemberResponse>> getBirthdayMembers(
            @PathVariable("departmentId") UUID departmentId,
            @RequestParam("month") int month) {
        List<Member> members = departmentQueryUseCase.getBirthdayMembers(
                DepartmentId.from(departmentId), month);
        List<BirthdayMemberResponse> response = members.stream()
                .map(m -> BirthdayMemberResponse.builder()
                        .memberId(m.getId().getValue())
                        .name(m.getName())
                        .birthday(m.getBirthday())
                        .sex(m.getSex())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get my departments in a church", description = "내가 속한 부서 목록")
    @GetMapping("/churches/{churchId}/my-departments")
    public ResponseEntity<List<DepartmentMemberResponse>> getMyDepartments(
            @PathVariable("churchId") UUID churchId,
            @AuthenticationPrincipal String memberId) {
        List<DepartmentMember> deptMembers = departmentQueryUseCase.getMyDepartments(
                MemberId.from(UUID.fromString(memberId)), ChurchId.from(churchId));

        // 부서 이름을 포함하여 응답
        List<Department> allDepts = departmentQueryUseCase.getDepartmentsByChurchId(ChurchId.from(churchId));
        Map<UUID, String> deptNameMap = allDepts.stream()
                .collect(Collectors.toMap(d -> d.getId().getValue(), Department::getName));

        List<DepartmentMemberResponse> response = deptMembers.stream()
                .map(dm -> DepartmentMemberResponse.from(dm, deptNameMap.get(dm.getDepartmentId().getValue())))
                .toList();
        return ResponseEntity.ok(response);
    }
}
