package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder
public class GroupMember extends DomainEntity<GroupMember, GroupMemberId> {

    private final GroupId groupId;
    private final Member member;
    private final GroupMemberRole role;
    private final GroupMemberStatus status;
} 