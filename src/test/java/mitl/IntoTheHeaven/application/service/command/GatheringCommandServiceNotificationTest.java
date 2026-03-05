package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringMemberCommand;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.NotificationType;
import mitl.IntoTheHeaven.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatheringCommandServiceNotificationTest {

    @Mock
    private GatheringPort gatheringPort;

    @Mock
    private MemberPort memberPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private GatheringCommandService gatheringCommandService;

    private UUID gatheringUuid;
    private UUID groupUuid;
    private UUID leaderMemberUuid;
    private UUID subLeaderMemberUuid;
    private UUID memberMemberUuid;
    private Gathering existingGathering;

    @BeforeEach
    void setUp() {
        gatheringUuid = UUID.randomUUID();
        groupUuid = UUID.randomUUID();
        leaderMemberUuid = UUID.randomUUID();
        subLeaderMemberUuid = UUID.randomUUID();
        memberMemberUuid = UUID.randomUUID();

        existingGathering = Gathering.builder()
                .id(GatheringId.from(gatheringUuid))
                .group(Group.builder().id(GroupId.from(groupUuid)).build())
                .name("소모임")
                .date(LocalDate.now())
                .adminComment(null)
                .gatheringMembers(List.of())
                .build();
    }

    private List<GroupMember> createGroupMembers() {
        Member leaderMember = Member.builder()
                .id(MemberId.from(leaderMemberUuid))
                .name("리더")
                .build();
        Member subLeaderMember = Member.builder()
                .id(MemberId.from(subLeaderMemberUuid))
                .name("서브리더")
                .build();
        Member normalMember = Member.builder()
                .id(MemberId.from(memberMemberUuid))
                .name("멤버")
                .build();

        return List.of(
                GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(groupUuid))
                        .member(leaderMember)
                        .role(GroupMemberRole.LEADER)
                        .status(GroupMemberStatus.ACTIVE)
                        .build(),
                GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(groupUuid))
                        .member(subLeaderMember)
                        .role(GroupMemberRole.SUB_LEADER)
                        .status(GroupMemberStatus.ACTIVE)
                        .build(),
                GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(groupUuid))
                        .member(normalMember)
                        .role(GroupMemberRole.MEMBER)
                        .status(GroupMemberStatus.ACTIVE)
                        .build()
        );
    }

    @Test
    @DisplayName("adminComment 변경 시 LEADER에게만 알림 생성")
    void updateGathering_adminCommentChanged_notifiesLeaderOnly() {
        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                null, null, null, null, null, null, null,
                "목회자 코멘트입니다"
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(createGroupMembers());
        when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                any(), any(), any(), any())).thenReturn(false);
        when(notificationPort.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        gatheringCommandService.updateGathering(command);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort, times(1)).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getReceiverId().getValue()).isEqualTo(leaderMemberUuid);
        assertThat(saved.getType()).isEqualTo(NotificationType.ADMIN_COMMENT);
        assertThat(saved.getEntityType()).isEqualTo("GATHERING");
        assertThat(saved.getEntityId()).isEqualTo(gatheringUuid.toString());
        assertThat(saved.getTargetUrl()).contains("/groups/" + groupUuid + "/gathering/" + gatheringUuid);
        assertThat(saved.isRead()).isFalse();
    }

    @Test
    @DisplayName("adminComment가 null이면 알림 미생성")
    void updateGathering_adminCommentNull_noNotification() {
        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                "이름변경", null, null, null, null, null, null,
                null
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

        gatheringCommandService.updateGathering(command);

        verify(notificationPort, never()).save(any());
    }

    @Test
    @DisplayName("adminComment가 빈 문자열이면 알림 미생성")
    void updateGathering_adminCommentBlank_noNotification() {
        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                null, null, null, null, null, null, null,
                "   "
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

        gatheringCommandService.updateGathering(command);

        verify(notificationPort, never()).save(any());
    }

    @Test
    @DisplayName("adminComment가 기존과 동일하면 알림 미생성")
    void updateGathering_adminCommentSame_noNotification() {
        String existingComment = "기존 코멘트";
        Gathering gatheringWithComment = existingGathering.toBuilder()
                .adminComment(existingComment)
                .build();

        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                null, null, null, null, null, null, null,
                existingComment
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(gatheringWithComment));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

        gatheringCommandService.updateGathering(command);

        verify(notificationPort, never()).save(any());
    }

    @Test
    @DisplayName("이미 미읽음 알림이 있으면 중복 생성 안 함")
    void updateGathering_duplicateNotification_skipped() {
        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                null, null, null, null, null, null, null,
                "새로운 코멘트"
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(createGroupMembers());
        when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                eq(leaderMemberUuid),
                eq(NotificationType.ADMIN_COMMENT.getValue()),
                eq("GATHERING"),
                eq(gatheringUuid.toString())
        )).thenReturn(true);

        gatheringCommandService.updateGathering(command);

        verify(notificationPort, never()).save(any());
    }

    // ─────────────── updateGatheringMember 알림 테스트 ───────────────

    private Gathering buildGatheringWithMember(UUID groupMemberUuid, UUID memberUuid, GroupMemberRole role) {
        Member member = Member.builder()
                .id(MemberId.from(memberUuid))
                .name("테스트멤버")
                .build();
        GroupMember groupMember = GroupMember.builder()
                .id(GroupMemberId.from(groupMemberUuid))
                .groupId(GroupId.from(groupUuid))
                .member(member)
                .role(role)
                .status(GroupMemberStatus.ACTIVE)
                .build();
        GatheringMember gatheringMember = GatheringMember.builder()
                .id(GatheringMemberId.from(UUID.randomUUID()))
                .gatheringId(GatheringId.from(gatheringUuid))
                .groupMember(groupMember)
                .worshipAttendance(false)
                .gatheringAttendance(false)
                .prayers(List.of())
                .build();
        return existingGathering.toBuilder()
                .gatheringMembers(List.of(gatheringMember))
                .build();
    }

    @Test
    @DisplayName("리더가 다른 멤버 카드 수정 → 알림 생성")
    void updateGatheringMember_leaderUpdatesOther_notificationSent() {
        UUID groupMemberUuid = UUID.randomUUID();
        UUID cardOwnerUuid = UUID.randomUUID();
        UUID callerUuid = leaderMemberUuid;

        Gathering gathering = buildGatheringWithMember(groupMemberUuid, cardOwnerUuid, GroupMemberRole.MEMBER);
        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(gathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                any(), any(), any(), any())).thenReturn(false);
        when(notificationPort.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                GatheringId.from(gatheringUuid),
                GroupMemberId.from(groupMemberUuid),
                MemberId.from(callerUuid),
                false, false, null, "", List.of()
        );

        gatheringCommandService.updateGatheringMember(command);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationPort, times(1)).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getReceiverId().getValue()).isEqualTo(cardOwnerUuid);
        assertThat(saved.getSenderId().getValue()).isEqualTo(callerUuid);
        assertThat(saved.getType()).isEqualTo(NotificationType.GATHERING_USER_CARD_UPDATED);
        assertThat(saved.getEntityType()).isEqualTo("GATHERING_MEMBER");
        assertThat(saved.getTargetUrl()).contains("/groups/" + groupUuid + "/gathering/" + gatheringUuid);
        assertThat(saved.isRead()).isFalse();
    }

    @Test
    @DisplayName("서브리더가 다른 멤버 카드 수정 → 알림 생성")
    void updateGatheringMember_subLeaderUpdatesOther_notificationSent() {
        UUID groupMemberUuid = UUID.randomUUID();
        UUID cardOwnerUuid = UUID.randomUUID();
        UUID callerUuid = subLeaderMemberUuid;

        Gathering gathering = buildGatheringWithMember(groupMemberUuid, cardOwnerUuid, GroupMemberRole.MEMBER);
        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(gathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                any(), any(), any(), any())).thenReturn(false);
        when(notificationPort.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                GatheringId.from(gatheringUuid),
                GroupMemberId.from(groupMemberUuid),
                MemberId.from(callerUuid),
                false, false, null, "", List.of()
        );

        gatheringCommandService.updateGatheringMember(command);

        verify(notificationPort, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("자기 자신 카드 수정 → 알림 생성 안 됨")
    void updateGatheringMember_selfUpdate_noNotification() {
        UUID groupMemberUuid = UUID.randomUUID();
        UUID cardOwnerUuid = memberMemberUuid;

        Gathering gathering = buildGatheringWithMember(groupMemberUuid, cardOwnerUuid, GroupMemberRole.MEMBER);
        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(gathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                GatheringId.from(gatheringUuid),
                GroupMemberId.from(groupMemberUuid),
                MemberId.from(cardOwnerUuid), // caller == owner
                false, false, null, "", List.of()
        );

        gatheringCommandService.updateGatheringMember(command);

        verify(notificationPort, never()).save(any());
    }

    @Test
    @DisplayName("이미 읽지 않은 동일 알림 존재 → 중복 생성 안 됨")
    void updateGatheringMember_duplicateUnreadNotification_skipped() {
        UUID groupMemberUuid = UUID.randomUUID();
        UUID cardOwnerUuid = UUID.randomUUID();
        UUID callerUuid = leaderMemberUuid;

        Gathering gathering = buildGatheringWithMember(groupMemberUuid, cardOwnerUuid, GroupMemberRole.MEMBER);
        GatheringMemberId gatheringMemberId = gathering.getGatheringMembers().get(0).getId();

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(gathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                eq(cardOwnerUuid),
                eq(NotificationType.GATHERING_USER_CARD_UPDATED.getValue()),
                eq("GATHERING_MEMBER"),
                eq(gatheringMemberId.getValue().toString())
        )).thenReturn(true);

        UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                GatheringId.from(gatheringUuid),
                GroupMemberId.from(groupMemberUuid),
                MemberId.from(callerUuid),
                false, false, null, "", List.of()
        );

        gatheringCommandService.updateGatheringMember(command);

        verify(notificationPort, never()).save(any());
    }

    @Test
    @DisplayName("GRADUATED 상태의 LEADER에게는 알림 미생성")
    void updateGathering_graduatedLeader_noNotification() {
        UUID graduatedLeaderUuid = UUID.randomUUID();
        Member graduatedLeaderMember = Member.builder()
                .id(MemberId.from(graduatedLeaderUuid))
                .name("졸업리더")
                .build();

        List<GroupMember> members = List.of(
                GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(groupUuid))
                        .member(graduatedLeaderMember)
                        .role(GroupMemberRole.LEADER)
                        .status(GroupMemberStatus.GRADUATED)
                        .build()
        );

        UpdateGatheringCommand command = new UpdateGatheringCommand(
                GatheringId.from(gatheringUuid),
                null, null, null, null, null, null, null,
                "코멘트"
        );

        when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
        when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(members);

        gatheringCommandService.updateGathering(command);

        verify(notificationPort, never()).save(any());
    }
}
