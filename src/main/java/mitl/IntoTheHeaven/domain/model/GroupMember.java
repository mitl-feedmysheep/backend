package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder
public class GroupMember extends DomainEntity<GroupMember, GroupMemberId> {

    private final GroupId groupId;
    private final Member member;  // MemberId 대신 Member 전체 정보
    private final GroupMemberRole role;
} 