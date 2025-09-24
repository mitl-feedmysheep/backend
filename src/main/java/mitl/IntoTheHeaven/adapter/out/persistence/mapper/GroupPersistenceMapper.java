package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupPersistenceMapper {

    private final MemberPersistenceMapper memberPersistenceMapper;
    private final MediaPersistenceMapper mediaPersistenceMapper;

    public Group toDomain(GroupJpaEntity entity) {
        return Group.builder()
                .id(GroupId.from(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .churchId(ChurchId.from(entity.getChurch().getId()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .medias(mediaPersistenceMapper.toDomainList(entity.getMedias()))
                .build();
    }

    public GroupJpaEntity toEntity(Group domain) {
        return GroupJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .description(domain.getDescription())
                .church(ChurchJpaEntity.builder().id(domain.getChurchId().getValue()).build())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .build();
    }

    public GroupMember toGroupMemberDomain(GroupMemberJpaEntity entity) {
        return GroupMember.builder()
                .id(GroupMemberId.from(entity.getId()))
                .groupId(GroupId.from(entity.getGroup().getId()))
                .member(memberPersistenceMapper.toDomain(entity.getMember()))
                .role(entity.getRole())
                .build();
    }

    public GroupMember toGroupMemberDomain(GroupMemberJpaEntity entity, UUID groupId) {
        return GroupMember.builder()
                .id(GroupMemberId.from(entity.getId()))
                .groupId(GroupId.from(groupId))
                .member(memberPersistenceMapper.toDomain(entity.getMember()))
                .role(entity.getRole())
                .build();
    }
}