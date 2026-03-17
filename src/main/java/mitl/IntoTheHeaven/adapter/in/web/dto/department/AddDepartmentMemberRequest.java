package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class AddDepartmentMemberRequest {

    @NotNull(message = "멤버 ID는 필수입니다.")
    private UUID memberId;

    private DepartmentRole role = DepartmentRole.MEMBER;
}
