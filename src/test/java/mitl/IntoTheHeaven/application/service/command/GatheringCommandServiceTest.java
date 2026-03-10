package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatheringCommandServiceTest {

    @Mock
    private GatheringPort gatheringPort;

    @Mock
    private MemberPort memberPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private GatheringCommandService gatheringCommandService;

    private UUID groupUuid;
    private GroupId groupId;
    private UUID gatheringUuid;
    private GatheringId gatheringId;

    @BeforeEach
    void setUp() {
        groupUuid = UUID.randomUUID();
        groupId = GroupId.from(groupUuid);
        gatheringUuid = UUID.randomUUID();
        gatheringId = GatheringId.from(gatheringUuid);
    }

    private GroupMember buildGroupMember(GroupMemberRole role, GroupMemberStatus status) {
        return GroupMember.builder()
                .id(GroupMemberId.from(UUID.randomUUID()))
                .groupId(groupId)
                .member(Member.builder()
                        .id(MemberId.from(UUID.randomUUID()))
                        .name("member")
                        .build())
                .role(role)
                .status(status)
                .build();
    }

    @Nested
    @DisplayName("createGathering - 모임 생성")
    class CreateGatheringTests {

        @Test
        @DisplayName("모든 그룹 멤버가 기본값으로 GatheringMember에 추가된다")
        void shouldAddAllGroupMembersWithDefaults() {
            GroupMember gm1 = buildGroupMember(GroupMemberRole.LEADER, GroupMemberStatus.ACTIVE);
            GroupMember gm2 = buildGroupMember(GroupMemberRole.MEMBER, GroupMemberStatus.ACTIVE);
            List<GroupMember> groupMembers = List.of(gm1, gm2);

            CreateGatheringCommand command = new CreateGatheringCommand(
                    groupId, "주간모임", "설명", LocalDate.of(2025, 6, 1),
                    Instant.now(), Instant.now().plusSeconds(3600), "교회");

            when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(groupMembers);
            when(gatheringPort.save(any(Gathering.class), eq(groupUuid)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Gathering result = gatheringCommandService.createGathering(command);

            ArgumentCaptor<Gathering> captor = ArgumentCaptor.forClass(Gathering.class);
            verify(gatheringPort).save(captor.capture(), eq(groupUuid));
            Gathering saved = captor.getValue();

            assertThat(saved.getGatheringMembers()).hasSize(2);
            saved.getGatheringMembers().forEach(gm -> {
                assertThat(gm.isWorshipAttendance()).isFalse();
                assertThat(gm.isGatheringAttendance()).isFalse();
                assertThat(gm.getGoal()).isNull();
                assertThat(gm.getStory()).isNull();
                assertThat(gm.getPrayers()).isEmpty();
                assertThat(gm.getId()).isNotNull();
            });
        }

        @Test
        @DisplayName("커맨드의 모든 필드가 Gathering에 매핑된다")
        void shouldMapAllCommandFields() {
            LocalDate date = LocalDate.of(2025, 7, 15);
            Instant start = Instant.parse("2025-07-15T10:00:00Z");
            Instant end = Instant.parse("2025-07-15T12:00:00Z");

            CreateGatheringCommand command = new CreateGatheringCommand(
                    groupId, "이름", "설명", date, start, end, "장소");

            when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(List.of());
            when(gatheringPort.save(any(Gathering.class), eq(groupUuid)))
                    .thenAnswer(inv -> inv.getArgument(0));

            gatheringCommandService.createGathering(command);

            ArgumentCaptor<Gathering> captor = ArgumentCaptor.forClass(Gathering.class);
            verify(gatheringPort).save(captor.capture(), eq(groupUuid));
            Gathering saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("이름");
            assertThat(saved.getDescription()).isEqualTo("설명");
            assertThat(saved.getDate()).isEqualTo(date);
            assertThat(saved.getStartedAt()).isEqualTo(start);
            assertThat(saved.getEndedAt()).isEqualTo(end);
            assertThat(saved.getPlace()).isEqualTo("장소");
        }
    }

    @Nested
    @DisplayName("updateGatheringMember - 모임 멤버 업데이트")
    class UpdateGatheringMemberTests {

        private Gathering existingGathering;
        private GatheringMember targetGatheringMember;
        private GroupMember groupMember;
        private MemberId ownerMemberId;
        private GroupMemberId groupMemberId;

        @BeforeEach
        void setUpGathering() {
            ownerMemberId = MemberId.from(UUID.randomUUID());
            groupMemberId = GroupMemberId.from(UUID.randomUUID());

            groupMember = GroupMember.builder()
                    .id(groupMemberId)
                    .groupId(groupId)
                    .member(Member.builder().id(ownerMemberId).name("홍길동").build())
                    .role(GroupMemberRole.MEMBER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();

            PrayerId existingPrayerId = PrayerId.from(UUID.randomUUID());
            Prayer existingPrayer = Prayer.builder()
                    .id(existingPrayerId)
                    .member(groupMember.getMember())
                    .gatheringMember(null)
                    .prayerRequest("기존 기도제목")
                    .description("기존 설명")
                    .isAnswered(true)
                    .build();

            targetGatheringMember = GatheringMember.builder()
                    .id(GatheringMemberId.from(UUID.randomUUID()))
                    .gatheringId(gatheringId)
                    .groupMember(groupMember)
                    .worshipAttendance(false)
                    .gatheringAttendance(false)
                    .goal(null)
                    .story(null)
                    .prayers(List.of(existingPrayer))
                    .build();

            existingGathering = Gathering.builder()
                    .id(gatheringId)
                    .group(Group.builder().id(groupId).name("1목장").build())
                    .name("모임")
                    .date(LocalDate.of(2025, 6, 1))
                    .gatheringMembers(new ArrayList<>(List.of(targetGatheringMember)))
                    .build();
        }

        @Test
        @DisplayName("기존 기도제목 업데이트 시 isAnswered가 보존된다")
        void shouldPreserveIsAnsweredForExistingPrayer() {
            Prayer existingPrayer = targetGatheringMember.getPrayers().get(0);
            UUID existingPrayerUuid = existingPrayer.getId().getValue();

            UpdateGatheringMemberCommand.PrayerUpdateCommand prayerCmd =
                    new UpdateGatheringMemberCommand.PrayerUpdateCommand(
                            existingPrayerUuid, "수정된 기도제목", "수정된 설명");

            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, ownerMemberId,
                    true, true, "목표", "이야기", List.of(prayerCmd));

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            GatheringMember result = gatheringCommandService.updateGatheringMember(command);

            assertThat(result.getPrayers()).hasSize(1);
            Prayer updatedPrayer = result.getPrayers().get(0);
            assertThat(updatedPrayer.getPrayerRequest()).isEqualTo("수정된 기도제목");
            assertThat(updatedPrayer.isAnswered()).isTrue();
            assertThat(updatedPrayer.getId()).isEqualTo(existingPrayer.getId());
        }

        @Test
        @DisplayName("새 기도제목은 isAnswered=false로 생성된다")
        void shouldSetIsAnsweredFalseForNewPrayer() {
            UpdateGatheringMemberCommand.PrayerUpdateCommand newPrayer =
                    new UpdateGatheringMemberCommand.PrayerUpdateCommand(
                            null, "새 기도제목", "새 설명");

            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, ownerMemberId,
                    true, true, "목표", "이야기", List.of(newPrayer));

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            GatheringMember result = gatheringCommandService.updateGatheringMember(command);

            assertThat(result.getPrayers()).hasSize(1);
            assertThat(result.getPrayers().get(0).isAnswered()).isFalse();
            assertThat(result.getPrayers().get(0).getPrayerRequest()).isEqualTo("새 기도제목");
        }

        @Test
        @DisplayName("요청에 없는 기도제목은 orphan removal로 제거된다")
        void shouldRemoveOrphanedPrayers() {
            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, ownerMemberId,
                    true, true, "목표", "이야기", List.of());

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            GatheringMember result = gatheringCommandService.updateGatheringMember(command);

            assertThat(result.getPrayers()).isEmpty();
        }

        @Test
        @DisplayName("호출자가 카드 소유자가 아니면 알림이 전송된다")
        void shouldSendNotificationWhenCallerIsNotOwner() {
            MemberId callerId = MemberId.from(UUID.randomUUID());

            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, callerId,
                    true, false, null, null, List.of());

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
            when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                    eq(ownerMemberId.getValue()),
                    eq(NotificationType.GATHERING_USER_CARD_UPDATED.getValue()),
                    eq("GATHERING_MEMBER"),
                    any())).thenReturn(false);

            gatheringCommandService.updateGatheringMember(command);

            verify(notificationPort).save(any(Notification.class));
        }

        @Test
        @DisplayName("호출자가 카드 소유자이면 알림이 전송되지 않는다")
        void shouldNotSendNotificationWhenCallerIsOwner() {
            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, ownerMemberId,
                    true, false, null, null, List.of());

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            gatheringCommandService.updateGatheringMember(command);

            verify(notificationPort, never()).save(any());
        }

        @Test
        @DisplayName("이미 읽지 않은 알림이 있으면 중복 알림이 전송되지 않는다")
        void shouldNotSendDuplicateNotification() {
            MemberId callerId = MemberId.from(UUID.randomUUID());

            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, callerId,
                    true, false, null, null, List.of());

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
            when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                    eq(ownerMemberId.getValue()),
                    eq(NotificationType.GATHERING_USER_CARD_UPDATED.getValue()),
                    eq("GATHERING_MEMBER"),
                    any())).thenReturn(true);

            gatheringCommandService.updateGatheringMember(command);

            verify(notificationPort, never()).save(any());
        }

        @Test
        @DisplayName("모임이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenGatheringNotFound() {
            UpdateGatheringMemberCommand command = new UpdateGatheringMemberCommand(
                    gatheringId, groupMemberId, ownerMemberId,
                    true, true, null, null, List.of());

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> gatheringCommandService.updateGatheringMember(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Gathering not found");
        }
    }

    @Nested
    @DisplayName("updateGathering - 모임 정보 업데이트")
    class UpdateGatheringTests {

        private Gathering existingGathering;
        private MemberId leaderMemberId;

        @BeforeEach
        void setUpExistingGathering() {
            leaderMemberId = MemberId.from(UUID.randomUUID());

            GroupMember leaderGroupMember = GroupMember.builder()
                    .id(GroupMemberId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .member(Member.builder().id(leaderMemberId).name("리더").build())
                    .role(GroupMemberRole.LEADER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();

            existingGathering = Gathering.builder()
                    .id(gatheringId)
                    .group(Group.builder().id(groupId).name("1목장").build())
                    .name("기존 모임")
                    .description("기존 설명")
                    .date(LocalDate.of(2025, 6, 1))
                    .place("기존 장소")
                    .adminComment("기존 코멘트")
                    .gatheringMembers(List.of())
                    .build();
        }

        @Test
        @DisplayName("null 필드는 기존 값을 유지한다")
        void shouldPreserveExistingValuesForNullFields() {
            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, null, null, null, null, null, null, null, null);

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            Gathering result = gatheringCommandService.updateGathering(command);

            ArgumentCaptor<Gathering> captor = ArgumentCaptor.forClass(Gathering.class);
            verify(gatheringPort).save(captor.capture());
            Gathering saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("기존 모임");
            assertThat(saved.getDescription()).isEqualTo("기존 설명");
            assertThat(saved.getPlace()).isEqualTo("기존 장소");
            assertThat(saved.getAdminComment()).isEqualTo("기존 코멘트");
        }

        @Test
        @DisplayName("adminComment가 변경되면 ACTIVE LEADER에게 알림을 전송한다")
        void shouldSendNotificationToActiveLeadersWhenAdminCommentChanged() {
            GroupMember leader = GroupMember.builder()
                    .id(GroupMemberId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .member(Member.builder().id(leaderMemberId).name("리더").build())
                    .role(GroupMemberRole.LEADER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();

            GroupMember subLeader = GroupMember.builder()
                    .id(GroupMemberId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .member(Member.builder().id(MemberId.from(UUID.randomUUID())).name("부리더").build())
                    .role(GroupMemberRole.SUB_LEADER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();

            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, null, null, null, null, null, null, null, "새로운 코멘트");

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
            when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(List.of(leader, subLeader));
            when(notificationPort.existsUnreadByReceiverAndTypeAndEntity(
                    eq(leaderMemberId.getValue()),
                    eq(NotificationType.ADMIN_COMMENT.getValue()),
                    eq("GATHERING"),
                    any())).thenReturn(false);

            gatheringCommandService.updateGathering(command);

            verify(notificationPort, times(1)).save(any(Notification.class));
        }

        @Test
        @DisplayName("빈 문자열/공백 adminComment는 알림을 전송하지 않는다")
        void shouldNotSendNotificationForBlankAdminComment() {
            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, null, null, null, null, null, null, null, "   ");

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            gatheringCommandService.updateGathering(command);

            verify(notificationPort, never()).save(any());
        }

        @Test
        @DisplayName("기존과 동일한 adminComment는 알림을 전송하지 않는다")
        void shouldNotSendNotificationWhenAdminCommentSame() {
            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, null, null, null, null, null, null, null, "기존 코멘트");

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));

            gatheringCommandService.updateGathering(command);

            verify(notificationPort, never()).save(any());
        }

        @Test
        @DisplayName("GRADUATED 상태의 LEADER에게는 알림을 전송하지 않는다")
        void shouldNotNotifyInactiveLeaders() {
            GroupMember graduatedLeader = GroupMember.builder()
                    .id(GroupMemberId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .member(Member.builder().id(MemberId.from(UUID.randomUUID())).name("졸업리더").build())
                    .role(GroupMemberRole.LEADER)
                    .status(GroupMemberStatus.GRADUATED)
                    .build();

            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, null, null, null, null, null, null, null, "새 코멘트");

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.of(existingGathering));
            when(gatheringPort.save(any(Gathering.class))).thenAnswer(inv -> inv.getArgument(0));
            when(memberPort.findGroupMembersByGroupId(groupUuid)).thenReturn(List.of(graduatedLeader));

            gatheringCommandService.updateGathering(command);

            verify(notificationPort, never()).save(any());
        }

        @Test
        @DisplayName("모임이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenGatheringNotFound() {
            UpdateGatheringCommand command = new UpdateGatheringCommand(
                    gatheringId, "이름", null, null, null, null, null, null, null);

            when(gatheringPort.findDetailById(gatheringUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> gatheringCommandService.updateGathering(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Gathering not found");
        }
    }
}
