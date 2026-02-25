package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;

@Getter
@RequiredArgsConstructor
public class GraduateMemberCommand {

    private final GroupId groupId;
    private final GroupMemberId groupMemberId;
    private final GroupId targetGroupId;
}
