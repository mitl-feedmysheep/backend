package mitl.IntoTheHeaven.adapter.in.web.dto.group;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;

@Getter
@Setter
public class ChangeGroupMemberRoleRequest {

    /**
     * New role to assign to the target group member
     */
    @NotNull(message = "New role is required")
    private GroupMemberRole newRole;
}


