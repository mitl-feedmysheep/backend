package mitl.IntoTheHeaven.adapter.in.web.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateDepartmentRequest {

    @NotBlank(message = "부서 이름은 필수입니다.")
    @Size(max = 50, message = "부서 이름은 50자 이내여야 합니다.")
    private String name;

    @Size(max = 200, message = "설명은 200자 이내여야 합니다.")
    private String description;
}
