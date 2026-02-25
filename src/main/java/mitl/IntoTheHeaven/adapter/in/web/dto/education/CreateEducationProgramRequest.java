package mitl.IntoTheHeaven.adapter.in.web.dto.education;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateEducationProgramRequest {

    @NotBlank(message = "Education program name is required")
    private String name;

    private String description;

    @Min(value = 1, message = "Total weeks must be at least 1")
    private int totalWeeks;
}
