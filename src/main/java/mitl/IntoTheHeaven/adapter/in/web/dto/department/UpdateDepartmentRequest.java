package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDepartmentRequest {

    @Size(max = 50, message = "부서 이름은 50자 이내여야 합니다.")
    private String name;

    @Size(max = 200, message = "설명은 200자 이내여야 합니다.")
    private String description;
}
