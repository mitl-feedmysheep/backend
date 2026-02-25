package mitl.IntoTheHeaven.adapter.in.web.dto.education;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RecordEducationProgressRequest {

    @NotNull(message = "Group member ID is required")
    private UUID groupMemberId;

    @Min(value = 1, message = "Week number must be at least 1")
    private int weekNumber;
}
