package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.GroupCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.ChangeGroupMemberRoleCommand;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCommandService implements GroupCommandUseCase {

    private final GroupPort groupPort;

    @Override
    public GroupMember changeGroupMemberRole(ChangeGroupMemberRoleCommand command) {
        GroupId groupId = command.getGroupId();
        GroupMemberId groupMemberId = command.getGroupMemberId();
        MemberId requesterId = command.getRequesterMemberId();
        GroupMemberRole newRole = command.getNewRole();

        // 1. Role Validation
        if (newRole != GroupMemberRole.SUB_LEADER && newRole != GroupMemberRole.MEMBER) {
            throw new IllegalArgumentException("newRole must be SUB_LEADER or MEMBER");
        }

        // 2. Requester Validation
        GroupMember requester = groupPort.findGroupMemberByGroupIdAndMemberId(groupId.getValue(), requesterId.getValue());
        if (requester.getRole() != GroupMemberRole.LEADER) {
            throw new IllegalStateException("Only group leader can change roles");
        }
        if (requesterId.getValue().equals(groupMemberId.getValue())) {
            throw new IllegalStateException("Leader cannot change own role");
        }

        // 3. Target Member Validation
        GroupMember targetMember = groupPort.findGroupMemberByGroupMemberId(groupMemberId.getValue());
        if (targetMember == null) {
            throw new IllegalArgumentException("Target member does not exist");
        }
        if (targetMember.getRole() == GroupMemberRole.LEADER) {
            throw new IllegalStateException("Leader cannot change role");
        }

        GroupMember updated = groupPort.updateGroupMemberRole(groupMemberId.getValue(), newRole);
        return updated;
    }
}


