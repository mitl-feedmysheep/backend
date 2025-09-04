package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;

import java.util.List;
import java.util.UUID;

public interface GroupPort {
    List<Group> findGroupsByMemberId(UUID memberId);
    List<Group> findGroupsByMemberIdAndChurchId(UUID memberId, UUID churchId);
    List<GroupMember> findGroupMembersByGroupId(UUID groupId);
    GroupMember findGroupMemberByGroupIdAndMemberId(UUID groupId, UUID groupMemberId);
    GroupMember findGroupMemberByGroupMemberId(UUID groupMemberId);
    GroupMember updateGroupMemberRole(UUID groupMemberId, GroupMemberRole newRole);
} 