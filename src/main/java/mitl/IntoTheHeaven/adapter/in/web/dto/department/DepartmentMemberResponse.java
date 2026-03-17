package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;

import java.util.List;
import java.util.UUID;

@Getter
public class DepartmentMemberResponse {
    private final UUID id;
    private final UUID departmentId;
    private final String departmentName;
    private final UUID memberId;
    private final String memberName;
    private final DepartmentRole role;
    private final DepartmentMemberStatus status;

    @Builder
    public DepartmentMemberResponse(UUID id, UUID departmentId, String departmentName, UUID memberId, String memberName,
                                     DepartmentRole role, DepartmentMemberStatus status) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.role = role;
        this.status = status;
    }

    public static DepartmentMemberResponse from(DepartmentMember dm) {
        return DepartmentMemberResponse.builder()
                .id(dm.getId().getValue())
                .departmentId(dm.getDepartmentId().getValue())
                .memberId(dm.getMember().getId().getValue())
                .memberName(dm.getMember().getName())
                .role(dm.getRole())
                .status(dm.getStatus())
                .build();
    }

    public static DepartmentMemberResponse from(DepartmentMember dm, String departmentName) {
        return DepartmentMemberResponse.builder()
                .id(dm.getId().getValue())
                .departmentId(dm.getDepartmentId().getValue())
                .departmentName(departmentName)
                .memberId(dm.getMember().getId().getValue())
                .memberName(dm.getMember().getName())
                .role(dm.getRole())
                .status(dm.getStatus())
                .build();
    }

    public static List<DepartmentMemberResponse> from(List<DepartmentMember> members) {
        return members.stream()
                .map(DepartmentMemberResponse::from)
                .toList();
    }
}
