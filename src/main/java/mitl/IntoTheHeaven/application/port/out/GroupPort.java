package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;

import java.util.List;
import java.util.UUID;

public interface GroupPort {
    List<Group> findGroupsByMemberId(UUID memberId);
    List<Group> findGroupsByMemberIdAndChurchId(UUID memberId, UUID churchId);
    List<GroupMember> findGroupMembersByGroupId(UUID groupId);
} 