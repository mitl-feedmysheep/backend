package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.ChangeGroupMemberRoleCommand;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupCommandServiceTest {

    @Mock
    private GroupPort groupPort;

    @InjectMocks
    private GroupCommandService groupCommandService;

    private GroupId groupId;
    private UUID groupUuid;
    private MemberId requesterMemberId;
    private UUID requesterMemberUuid;
    private GroupMemberId targetGroupMemberId;
    private UUID targetGroupMemberUuid;

    @BeforeEach
    void setUp() {
        groupUuid = UUID.randomUUID();
        groupId = GroupId.from(groupUuid);
        requesterMemberUuid = UUID.randomUUID();
        requesterMemberId = MemberId.from(requesterMemberUuid);
        targetGroupMemberUuid = UUID.randomUUID();
        targetGroupMemberId = GroupMemberId.from(targetGroupMemberUuid);
    }

    private GroupMember buildGroupMember(GroupMemberId id, MemberId memberId, GroupMemberRole role) {
        return GroupMember.builder()
                .id(id)
                .groupId(groupId)
                .member(Member.builder().id(memberId).name("test").build())
                .role(role)
                .status(GroupMemberStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("changeGroupMemberRole - 그룹 멤버 역할 변경")
    class ChangeGroupMemberRoleTests {

        @Test
        @DisplayName("LEADER가 MEMBER의 역할을 SUB_LEADER로 변경 성공")
        void shouldChangeRoleSuccessfully() {
            GroupMember requester = buildGroupMember(
                    GroupMemberId.from(requesterMemberUuid), requesterMemberId, GroupMemberRole.LEADER);
            GroupMember target = buildGroupMember(
                    targetGroupMemberId, MemberId.from(UUID.randomUUID()), GroupMemberRole.MEMBER);
            GroupMember updated = buildGroupMember(
                    targetGroupMemberId, MemberId.from(UUID.randomUUID()), GroupMemberRole.SUB_LEADER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.SUB_LEADER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);
            when(groupPort.findGroupMemberByGroupMemberId(targetGroupMemberUuid))
                    .thenReturn(target);
            when(groupPort.updateGroupMemberRole(targetGroupMemberUuid, GroupMemberRole.SUB_LEADER))
                    .thenReturn(updated);

            GroupMember result = groupCommandService.changeGroupMemberRole(command);

            assertThat(result.getRole()).isEqualTo(GroupMemberRole.SUB_LEADER);
            verify(groupPort).updateGroupMemberRole(targetGroupMemberUuid, GroupMemberRole.SUB_LEADER);
        }

        @Test
        @DisplayName("newRole이 LEADER이면 IllegalArgumentException 발생")
        void shouldThrowWhenNewRoleIsLeader() {
            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.LEADER);

            assertThatThrownBy(() -> groupCommandService.changeGroupMemberRole(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("newRole must be SUB_LEADER or MEMBER");

            verify(groupPort, never()).updateGroupMemberRole(any(), any());
        }

        @Test
        @DisplayName("요청자가 LEADER가 아니면 IllegalStateException 발생")
        void shouldThrowWhenRequesterIsNotLeader() {
            GroupMember requester = buildGroupMember(
                    GroupMemberId.from(requesterMemberUuid), requesterMemberId, GroupMemberRole.SUB_LEADER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.MEMBER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);

            assertThatThrownBy(() -> groupCommandService.changeGroupMemberRole(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Only group leader can change roles");
        }

        @Test
        @DisplayName("자기 자신의 역할을 변경하려고 하면 IllegalStateException 발생")
        void shouldThrowWhenChangingOwnRole() {
            GroupMemberId sameId = GroupMemberId.from(requesterMemberUuid);
            GroupMember requester = buildGroupMember(sameId, requesterMemberId, GroupMemberRole.LEADER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, sameId, requesterMemberId, GroupMemberRole.MEMBER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);

            assertThatThrownBy(() -> groupCommandService.changeGroupMemberRole(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Leader cannot change own role");
        }

        @Test
        @DisplayName("대상 멤버가 존재하지 않으면 IllegalArgumentException 발생")
        void shouldThrowWhenTargetMemberNotFound() {
            GroupMember requester = buildGroupMember(
                    GroupMemberId.from(requesterMemberUuid), requesterMemberId, GroupMemberRole.LEADER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.SUB_LEADER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);
            when(groupPort.findGroupMemberByGroupMemberId(targetGroupMemberUuid))
                    .thenReturn(null);

            assertThatThrownBy(() -> groupCommandService.changeGroupMemberRole(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Target member does not exist");
        }

        @Test
        @DisplayName("대상 멤버가 LEADER이면 역할 변경 불가 - IllegalStateException 발생")
        void shouldThrowWhenTargetIsLeader() {
            GroupMember requester = buildGroupMember(
                    GroupMemberId.from(requesterMemberUuid), requesterMemberId, GroupMemberRole.LEADER);
            GroupMember leaderTarget = buildGroupMember(
                    targetGroupMemberId, MemberId.from(UUID.randomUUID()), GroupMemberRole.LEADER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.MEMBER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);
            when(groupPort.findGroupMemberByGroupMemberId(targetGroupMemberUuid))
                    .thenReturn(leaderTarget);

            assertThatThrownBy(() -> groupCommandService.changeGroupMemberRole(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Leader cannot change role");
        }

        @Test
        @DisplayName("newRole이 MEMBER이면 SUB_LEADER에서 MEMBER로 변경 성공")
        void shouldDemoteSubLeaderToMember() {
            GroupMember requester = buildGroupMember(
                    GroupMemberId.from(requesterMemberUuid), requesterMemberId, GroupMemberRole.LEADER);
            GroupMember target = buildGroupMember(
                    targetGroupMemberId, MemberId.from(UUID.randomUUID()), GroupMemberRole.SUB_LEADER);
            GroupMember updated = buildGroupMember(
                    targetGroupMemberId, MemberId.from(UUID.randomUUID()), GroupMemberRole.MEMBER);

            ChangeGroupMemberRoleCommand command = new ChangeGroupMemberRoleCommand(
                    groupId, targetGroupMemberId, requesterMemberId, GroupMemberRole.MEMBER);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupUuid, requesterMemberUuid))
                    .thenReturn(requester);
            when(groupPort.findGroupMemberByGroupMemberId(targetGroupMemberUuid))
                    .thenReturn(target);
            when(groupPort.updateGroupMemberRole(targetGroupMemberUuid, GroupMemberRole.MEMBER))
                    .thenReturn(updated);

            GroupMember result = groupCommandService.changeGroupMemberRole(command);

            assertThat(result.getRole()).isEqualTo(GroupMemberRole.MEMBER);
        }
    }
}
