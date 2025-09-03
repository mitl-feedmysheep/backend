package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.ChangeGroupMemberRoleRequest;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ChangeGroupMemberRoleCommand {

    private final GroupId groupId;
    private final MemberId targetMemberId;
    private final MemberId requesterMemberId;
    private final GroupMemberRole newRole;

    public static ChangeGroupMemberRoleCommand from(UUID groupId,
                                                    UUID targetMemberId,
                                                    UUID requesterMemberId,
                                                    ChangeGroupMemberRoleRequest request) {
        return new ChangeGroupMemberRoleCommand(
                GroupId.from(groupId),
                MemberId.from(targetMemberId),
                MemberId.from(requesterMemberId),
                request.getNewRole()
        );
    }
}


