package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.ChangeGroupMemberRoleCommand;
import mitl.IntoTheHeaven.domain.model.GroupMember;

public interface GroupCommandUseCase {
    GroupMember changeGroupMemberRole(ChangeGroupMemberRoleCommand command);
}


