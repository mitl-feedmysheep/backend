package mitl.IntoTheHeaven.adapter.in.web.dto.education;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class GraduateMemberRequest {

    @NotNull(message = "Group member ID is required")
    private UUID groupMemberId;

    @NotNull(message = "Target group ID is required")
    private UUID targetGroupId;
}
