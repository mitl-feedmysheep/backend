package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GroupJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GroupMemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MemberJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.GroupPersistenceMapper;
import mitl.IntoTheHeaven.application.dto.GroupWithLeader;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupPersistenceAdapter implements GroupPort {

        private final MemberJpaRepository memberJpaRepository;
        private final GroupJpaRepository groupJpaRepository;
        private final GroupMemberJpaRepository groupMemberJpaRepository;
        private final GroupPersistenceMapper groupPersistenceMapper;

        @Override
        public List<Group> findGroupsByMemberId(UUID memberId) {
                return memberJpaRepository.findWithGroupsById(memberId)
                                .map(member -> member.getGroupMembers().stream()
                                                .filter(gm -> gm.getStatus() == GroupMemberStatus.ACTIVE)
                                                .map(groupMember -> groupPersistenceMapper
                                                                .toDomain(groupMember.getGroup()))
                                                .toList())
                                .orElse(Collections.emptyList());
        }

        @Override
        public List<Group> findGroupsByMemberIdAndChurchId(UUID memberId, UUID churchId) {
                return memberJpaRepository.findWithGroupsAndChurchesById(memberId)
                                .map(member -> member.getGroupMembers().stream()
                                                .filter(gm -> gm.getStatus() == GroupMemberStatus.ACTIVE)
                                                .filter(groupMember -> groupMember.getGroup().getChurch().getId()
                                                                .equals(churchId))
                                                .map(groupMember -> groupPersistenceMapper
                                                                .toDomain(groupMember.getGroup()))
                                                .sorted(Comparator.comparing(Group::getEndDate,
                                                                Comparator.nullsLast(Comparator.reverseOrder())))
                                                .toList())
                                .orElse(Collections.emptyList());
        }

        @Override
        public List<GroupMember> findGroupMembersByGroupId(UUID groupId) {
                return groupMemberJpaRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE).stream()
                                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupId))
                                .toList();
        }

        @Override
        public List<GroupMember> findAllGroupMembersByGroupId(UUID groupId) {
                return groupMemberJpaRepository.findByGroupId(groupId).stream()
                                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupId))
                                .toList();
        }

        @Override
        public GroupMember findGroupMemberByGroupIdAndMemberId(UUID groupId, UUID groupMemberId) {
                return groupMemberJpaRepository.findByGroup_IdAndMember_Id(groupId, groupMemberId)
                                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupId))
                                .orElseThrow(() -> new RuntimeException("GroupMember not found for groupId: " + groupId
                                                + ", groupMemberId: " + groupMemberId));
        }

        @Override
        public GroupMember findGroupMemberByGroupMemberId(UUID groupMemberId) {
                return groupMemberJpaRepository.findById(groupMemberId)
                                .map(entity -> groupPersistenceMapper.toGroupMemberDomain(entity, groupMemberId))
                                .orElseThrow(() -> new RuntimeException(
                                                "GroupMember not found for groupMemberId: " + groupMemberId));
        }

        @Override
        public GroupMember updateGroupMemberRole(UUID groupMemberId, GroupMemberRole newRole) {
                GroupMemberJpaEntity entity = groupMemberJpaRepository.findById(groupMemberId)
                                .orElseThrow(() -> new RuntimeException(
                                                "GroupMember not found for groupMemberId: " + groupMemberId));

                entity.setRole(newRole);

                entity = groupMemberJpaRepository.save(entity);
                return groupPersistenceMapper.toGroupMemberDomain(entity);
        }

        @Override
        public List<GroupWithLeader> findGroupsWithLeaderByChurchId(UUID churchId) {
                int currentYear = LocalDate.now().getYear();
                LocalDate yearStart = LocalDate.of(currentYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(currentYear, 12, 31);

                List<GroupJpaEntity> groups = groupJpaRepository
                                .findAllByChurchIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                                churchId, yearEnd, yearStart);

                return groups.stream()
                                .map(group -> {
                                        String leaderName = group.getGroupMembers().stream()
                                                        .filter(gm -> gm.getStatus() == GroupMemberStatus.ACTIVE)
                                                        .filter(gm -> gm.getRole() == GroupMemberRole.LEADER)
                                                        .findFirst()
                                                        .map(gm -> gm.getMember().getName())
                                                        .orElse(null);

                                        return GroupWithLeader.builder()
                                                        .groupId(group.getId())
                                                        .groupName(group.getName())
                                                        .leaderName(leaderName)
                                                        .build();
                                })
                                .sorted(Comparator.comparing(GroupWithLeader::getGroupName))
                                .toList();
        }
}