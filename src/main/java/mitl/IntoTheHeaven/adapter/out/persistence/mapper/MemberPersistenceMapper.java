package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {

    public Member toDomain(MemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Member.builder()
                .id(MemberId.from(entity.getId()))
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .sex(entity.getSex())
                .birthday(entity.getBirthday())
                .phone(entity.getPhone())
                .profileUrl(entity.getProfileUrl())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberJpaEntity toEntity(Member domain) {
        if (domain == null) {
            return null;
        }
        return MemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .sex(domain.getSex())
                .birthday(domain.getBirthday())
                .phone(domain.getPhone())
                .profileUrl(domain.getProfileUrl())
                .address(domain.getAddress())
                .description(domain.getDescription())
                .build();
    }

    public GroupMember toGroupMemberDomain(GroupMemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return GroupMember.builder()
                .id(GroupMemberId.from(entity.getId()))
                .groupId(GroupId.from(entity.getGroup().getId()))
                .member(toDomain(entity.getMember()))  // Member 전체 정보 포함
                .role(entity.getRole())
                .build();
    }

    public GroupMemberJpaEntity toGroupMemberEntity(GroupMember domain) {
        if (domain == null) {
            return null;
        }
        return GroupMemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .group(GroupJpaEntity.builder().id(domain.getGroupId().getValue()).build())
                .member(MemberJpaEntity.builder().id(domain.getMember().getId().getValue()).build())
                .role(domain.getRole())
                .build();
    }
} 