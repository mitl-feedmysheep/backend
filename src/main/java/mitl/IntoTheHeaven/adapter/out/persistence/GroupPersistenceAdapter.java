package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GroupMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.GroupPersistenceMapper;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupPersistenceAdapter implements GroupPort {

    private final MemberJpaRepository memberJpaRepository;
    private final GroupMemberJpaRepository groupMemberJpaRepository;
    private final GroupPersistenceMapper groupPersistenceMapper;

    @Override
    public List<Group> findGroupsByMemberId(UUID memberId) {
        return memberJpaRepository.findWithGroupsById(memberId)
                .map(member -> member.getGroupMembers().stream()
                        .map(groupMember -> groupPersistenceMapper.toDomain(groupMember.getGroup()))
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Group> findGroupsByMemberIdAndChurchId(UUID memberId, UUID churchId) {
        return memberJpaRepository.findWithGroupsAndChurchesById(memberId)
                .map(member -> member.getGroupMembers().stream()
                        .filter(groupMember -> groupMember.getGroup().getChurch().getId().equals(churchId))
                        .map(groupMember -> groupPersistenceMapper.toDomain(groupMember.getGroup()))
                        .toList())
                .orElse(Collections.emptyList());
    }

    @Override
    public List<GroupMember> findGroupMembersByGroupId(UUID groupId) {
        return groupMemberJpaRepository.findByGroupIdOrderByRoleAscMemberBirthdayAsc(groupId).stream()
                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupId))
                .toList();
    }

    @Override
    public GroupMember findGroupMemberByGroupIdAndMemberId(UUID groupId, UUID memberId) {
        return groupMemberJpaRepository.findByGroup_IdAndMember_Id(groupId, memberId)
                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupId))
                .orElseThrow(() -> new RuntimeException("GroupMember not found for groupId: " + groupId + ", memberId: " + memberId));
    }

    @Override
    public GroupMember updateGroupMemberRole(UUID groupId, UUID memberId, GroupMemberRole newRole) {
        GroupMemberJpaEntity entity = groupMemberJpaRepository.findByGroup_IdAndMember_Id(groupId, memberId)
                .orElseThrow(() -> new RuntimeException("GroupMember not found for groupId: " + groupId + ", memberId: " + memberId));
        entity = GroupMemberJpaEntity.builder()
                .id(entity.getId())
                .group(entity.getGroup())
                .member(entity.getMember())
                .role(newRole)
                .gatheringMembers(entity.getGatheringMembers())
                .build();
        entity = groupMemberJpaRepository.save(entity);
        return groupPersistenceMapper.toGroupMemberDomain(entity, groupId);
    }
} 