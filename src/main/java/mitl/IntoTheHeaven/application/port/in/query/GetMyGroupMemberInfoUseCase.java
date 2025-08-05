package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface GetMyGroupMemberInfoUseCase {
    GroupMember getMyGroupMemberInfo(GroupId groupId, MemberId memberId);
}