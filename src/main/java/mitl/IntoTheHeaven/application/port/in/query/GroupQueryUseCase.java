package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.UUID;

public interface GroupQueryUseCase {
    List<Group> getGroupsByMemberId(MemberId memberId);
    List<Group> getGroupsByMemberIdAndChurchId(MemberId memberId, ChurchId churchId);
    List<GroupMember> getGroupMembersByGroupId(UUID groupId);
} 