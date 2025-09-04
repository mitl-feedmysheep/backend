package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.ChangeGroupMemberRoleRequest;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;

@Getter
@RequiredArgsConstructor
public class ChangeGroupMemberRoleCommand {

    private final GroupId groupId;
    private final GroupMemberId groupMemberId;
    private final MemberId requesterMemberId;
    private final GroupMemberRole newRole;

    public static ChangeGroupMemberRoleCommand from(GroupId groupId,
            GroupMemberId groupMemberId,
            MemberId requesterMemberId,
            ChangeGroupMemberRoleRequest request) {
        return new ChangeGroupMemberRoleCommand(
                groupId,
                groupMemberId,
                requesterMemberId,
                request.getNewRole());
    }
}
