package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;

@Getter
@NoArgsConstructor
public class ChangeDepartmentMemberRoleRequest {

    @NotNull(message = "역할은 필수입니다.")
    private DepartmentRole role;
}
