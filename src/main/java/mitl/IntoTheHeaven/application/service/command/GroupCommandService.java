package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.GroupCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.ChangeGroupMemberRoleCommand;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupCommandService implements GroupCommandUseCase {

    private final GroupPort groupPort;

    @Override
    public GroupMember changeGroupMemberRole(ChangeGroupMemberRoleCommand command) {
        UUID groupId = command.getGroupId().getValue();
        UUID targetMemberId = command.getTargetMemberId().getValue();
        UUID requesterId = command.getRequesterMemberId().getValue();
        GroupMemberRole newRole = command.getNewRole();

        // 1. Role Validation
        if (newRole != GroupMemberRole.SUB_LEADER && newRole != GroupMemberRole.MEMBER) {
            throw new IllegalArgumentException("newRole must be SUB_LEADER or MEMBER");
        }

        // 2. Requester Validation
        GroupMember requester = groupPort.findGroupMemberByGroupIdAndMemberId(groupId, requesterId);
        if (requester.getRole() != GroupMemberRole.LEADER) {
            throw new IllegalStateException("Only group leader can change roles");
        }
        if (requesterId.equals(targetMemberId)) {
            throw new IllegalStateException("Leader cannot change own role");
        }

        // 3. Target Member Validation
        GroupMember targetMember = groupPort.findGroupMemberByGroupIdAndMemberId(groupId, targetMemberId);
        if (targetMember == null) {
            throw new IllegalArgumentException("Target member does not exist");
        }
        if (targetMember.getRole() == GroupMemberRole.LEADER) {
            throw new IllegalStateException("Leader cannot change role");
        }

        GroupMember updated = groupPort.updateGroupMemberRole(groupId, targetMemberId, newRole);
        return updated;
    }
}


