package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
public class GroupMember extends DomainEntity<GroupMember, GroupMemberId> {

    private final GroupId groupId;
    private final MemberId memberId;
    private final GroupMemberRole role;

    @Builder
    public GroupMember(GroupMemberId id, GroupId groupId, MemberId memberId, GroupMemberRole role) {
        super(id);
        this.groupId = groupId;
        this.memberId = memberId;
        this.role = role;
    }
} 