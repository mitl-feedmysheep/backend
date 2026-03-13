package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.AddDepartmentMemberRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.ChangeDepartmentMemberRoleRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.CreateDepartmentRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.DepartmentMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.DepartmentResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.department.UpdateDepartmentRequest;
import mitl.IntoTheHeaven.application.port.in.command.DepartmentCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.DepartmentQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
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
