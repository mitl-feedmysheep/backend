package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.global.common.BaseEntity;

@Entity
@Table(name = "group_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupJpaEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberJpaEntity member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupMemberRole role;

    @Builder
    public GroupMemberJpaEntity(GroupJpaEntity group, MemberJpaEntity member, GroupMemberRole role) {
        this.group = group;
        this.member = member;
        this.role = role;
    }
} 