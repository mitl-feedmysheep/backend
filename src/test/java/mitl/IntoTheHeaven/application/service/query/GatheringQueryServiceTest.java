package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.in.query.dto.GatheringWithStatistics;
import mitl.IntoTheHeaven.application.port.out.GatheringMemberData;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatheringQueryServiceTest {

    @Mock
    private GatheringPort gatheringPort;

    @InjectMocks
    private GatheringQueryService gatheringQueryService;

    private Member createMember(String name, LocalDate birthday) {
        return Member.builder()
                .id(MemberId.from(UUID.randomUUID()))
                .name(name)
                .email(name.toLowerCase() + "@test.com")
                .password("encoded")
                .sex(Sex.M)
                .birthday(birthday)
                .build();
    }

    private GroupMember createGroupMember(GroupMemberRole role, Member member) {
        return GroupMember.builder()
                .id(GroupMemberId.from(UUID.randomUUID()))
                .groupId(GroupId.from(UUID.randomUUID()))
                .member(member)
                .role(role)
                .status(GroupMemberStatus.ACTIVE)
                .build();
    }

    private GatheringMember createGatheringMember(GatheringId gatheringId, GroupMember groupMember) {
        return GatheringMember.builder()
                .id(GatheringMemberId.from(UUID.randomUUID()))
                .gatheringId(gatheringId)
                .groupMember(groupMember)
                .worshipAttendance(true)
                .gatheringAttendance(true)
                .prayers(List.of())
                .build();
    }

    private Gathering createGathering(GatheringId id, LocalDate date, Instant startedAt,
                                      List<GatheringMember> members) {
        return Gathering.builder()
                .id(id)
                .name("모임")
                .date(date)
                .startedAt(startedAt)
                .gatheringMembers(members)
                .build();
    }

    @Nested
    @DisplayName("getGatheringDetail")
    class GetGatheringDetail {

        @Test
        @DisplayName("모임 상세 조회 성공 - 멤버가 역할, 생년월일 순으로 정렬됨")
        void shouldReturnGatheringWithSortedMembers() {
            GatheringId gatheringId = GatheringId.from(UUID.randomUUID());

            Member leaderMember = createMember("리더", LocalDate.of(1990, 1, 1));
            Member regularOlder = createMember("연장자멤버", LocalDate.of(1985, 6, 15));
            Member regularYounger = createMember("막내멤버", LocalDate.of(1998, 12, 1));

            GroupMember leaderGm = createGroupMember(GroupMemberRole.LEADER, leaderMember);
            GroupMember regularOlderGm = createGroupMember(GroupMemberRole.MEMBER, regularOlder);
            GroupMember regularYoungerGm = createGroupMember(GroupMemberRole.MEMBER, regularYounger);

            List<GatheringMember> unsorted = List.of(
                    createGatheringMember(gatheringId, regularYoungerGm),
                    createGatheringMember(gatheringId, leaderGm),
                    createGatheringMember(gatheringId, regularOlderGm)
            );

            Gathering gathering = createGathering(gatheringId, LocalDate.now(), Instant.now(), unsorted);

            when(gatheringPort.findDetailById(gatheringId.getValue()))
                    .thenReturn(Optional.of(gathering));

            Gathering result = gatheringQueryService.getGatheringDetail(gatheringId);

            assertThat(result.getGatheringMembers()).hasSize(3);
            assertThat(result.getGatheringMembers().get(0).getGroupMember().getRole())
                    .isEqualTo(GroupMemberRole.LEADER);
            assertThat(result.getGatheringMembers().get(1).getGroupMember().getMember().getName())
                    .isEqualTo("연장자멤버");
            assertThat(result.getGatheringMembers().get(2).getGroupMember().getMember().getName())
                    .isEqualTo("막내멤버");
        }

        @Test
        @DisplayName("존재하지 않는 모임 조회 시 RuntimeException 발생")
        void shouldThrowExceptionWhenNotFound() {
            GatheringId gatheringId = GatheringId.from(UUID.randomUUID());

            when(gatheringPort.findDetailById(gatheringId.getValue())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> gatheringQueryService.getGatheringDetail(gatheringId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Gathering not found");
        }

        @Test
        @DisplayName("SUB_LEADER는 LEADER가 아니므로 일반 멤버와 같은 우선순위로 정렬")
        void shouldTreatSubLeaderAsNonLeader() {
            GatheringId gatheringId = GatheringId.from(UUID.randomUUID());

            Member leaderMember = createMember("리더", LocalDate.of(1990, 5, 1));
            Member subLeaderMember = createMember("부리더", LocalDate.of(1988, 3, 1));
            Member regularMember = createMember("멤버", LocalDate.of(1985, 1, 1));

            GroupMember leaderGm = createGroupMember(GroupMemberRole.LEADER, leaderMember);
            GroupMember subLeaderGm = createGroupMember(GroupMemberRole.SUB_LEADER, subLeaderMember);
            GroupMember regularGm = createGroupMember(GroupMemberRole.MEMBER, regularMember);

            List<GatheringMember> members = List.of(
                    createGatheringMember(gatheringId, regularGm),
                    createGatheringMember(gatheringId, subLeaderGm),
                    createGatheringMember(gatheringId, leaderGm)
            );

            Gathering gathering = createGathering(gatheringId, LocalDate.now(), Instant.now(), members);

            when(gatheringPort.findDetailById(gatheringId.getValue()))
                    .thenReturn(Optional.of(gathering));

            Gathering result = gatheringQueryService.getGatheringDetail(gatheringId);

            assertThat(result.getGatheringMembers().get(0).getGroupMember().getRole())
                    .isEqualTo(GroupMemberRole.LEADER);
            // SUB_LEADER and MEMBER both get priority 1, sorted by birthday
            assertThat(result.getGatheringMembers().get(1).getGroupMember().getMember().getName())
                    .isEqualTo("멤버");
            assertThat(result.getGatheringMembers().get(2).getGroupMember().getMember().getName())
                    .isEqualTo("부리더");
        }
    }

    @Nested
    @DisplayName("getGatheringsWithStatisticsByGroupId")
    class GetGatheringsWithStatisticsByGroupId {

        @Test
        @DisplayName("모임 목록을 날짜 내림차순으로 정렬하고 통계를 계산")
        void shouldSortByDateDescAndCalculateStatistics() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            GatheringId id1 = GatheringId.from(UUID.randomUUID());
            GatheringId id2 = GatheringId.from(UUID.randomUUID());
            GatheringId id3 = GatheringId.from(UUID.randomUUID());

            Instant now = Instant.now();
            Gathering g1 = createGathering(id1, LocalDate.of(2025, 1, 1), now, List.of());
            Gathering g2 = createGathering(id2, LocalDate.of(2025, 3, 1), now, List.of());
            Gathering g3 = createGathering(id3, LocalDate.of(2025, 2, 1), now, List.of());

            when(gatheringPort.findAllByGroupId(groupId.getValue()))
                    .thenReturn(List.of(g1, g2, g3));

            List<GatheringMemberData> memberData = List.of(
                    GatheringMemberData.builder()
                            .gatheringId(id2.getValue())
                            .worshipAttendance(true)
                            .gatheringAttendance(true)
                            .prayerIds(List.of(UUID.randomUUID()))
                            .build(),
                    GatheringMemberData.builder()
                            .gatheringId(id2.getValue())
                            .worshipAttendance(false)
                            .gatheringAttendance(true)
                            .prayerIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                            .build(),
                    GatheringMemberData.builder()
                            .gatheringId(id3.getValue())
                            .worshipAttendance(true)
                            .gatheringAttendance(false)
                            .prayerIds(List.of())
                            .build()
            );

            when(gatheringPort.findGatheringMemberDataByGatheringIds(anyList()))
                    .thenReturn(memberData);

            List<GatheringWithStatistics> result =
                    gatheringQueryService.getGatheringsWithStatisticsByGroupId(groupId);

            assertThat(result).hasSize(3);

            // Most recent first (2025-03-01)
            assertThat(result.get(0).getGathering().getDate()).isEqualTo(LocalDate.of(2025, 3, 1));
            assertThat(result.get(0).getNth()).isEqualTo(1);
            assertThat(result.get(0).getTotalWorshipAttendanceCount()).isEqualTo(1);
            assertThat(result.get(0).getTotalGatheringAttendanceCount()).isEqualTo(2);
            assertThat(result.get(0).getTotalPrayerRequestCount()).isEqualTo(3);

            // Second (2025-02-01)
            assertThat(result.get(1).getGathering().getDate()).isEqualTo(LocalDate.of(2025, 2, 1));
            assertThat(result.get(1).getNth()).isEqualTo(2);
            assertThat(result.get(1).getTotalWorshipAttendanceCount()).isEqualTo(1);
            assertThat(result.get(1).getTotalGatheringAttendanceCount()).isEqualTo(0);

            // Oldest (2025-01-01), no statistics
            assertThat(result.get(2).getGathering().getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(result.get(2).getNth()).isEqualTo(3);
            assertThat(result.get(2).getTotalWorshipAttendanceCount()).isEqualTo(0);
            assertThat(result.get(2).getTotalGatheringAttendanceCount()).isEqualTo(0);
            assertThat(result.get(2).getTotalPrayerRequestCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("같은 날짜의 모임은 시작 시간 내림차순으로 정렬")
        void shouldSortByStartedAtDescWhenSameDate() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            GatheringId id1 = GatheringId.from(UUID.randomUUID());
            GatheringId id2 = GatheringId.from(UUID.randomUUID());

            LocalDate sameDate = LocalDate.of(2025, 6, 15);
            Instant earlier = Instant.parse("2025-06-15T09:00:00Z");
            Instant later = Instant.parse("2025-06-15T14:00:00Z");

            Gathering gEarlier = createGathering(id1, sameDate, earlier, List.of());
            Gathering gLater = createGathering(id2, sameDate, later, List.of());

            when(gatheringPort.findAllByGroupId(groupId.getValue()))
                    .thenReturn(List.of(gEarlier, gLater));
            when(gatheringPort.findGatheringMemberDataByGatheringIds(anyList()))
                    .thenReturn(List.of());

            List<GatheringWithStatistics> result =
                    gatheringQueryService.getGatheringsWithStatisticsByGroupId(groupId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getGathering().getStartedAt()).isEqualTo(later);
            assertThat(result.get(1).getGathering().getStartedAt()).isEqualTo(earlier);
        }

        @Test
        @DisplayName("통계 데이터가 없는 모임은 모든 카운트가 0")
        void shouldDefaultToZeroWhenNoStatistics() {
            GroupId groupId = GroupId.from(UUID.randomUUID());
            GatheringId id = GatheringId.from(UUID.randomUUID());

            Gathering gathering = createGathering(id, LocalDate.now(), Instant.now(), List.of());

            when(gatheringPort.findAllByGroupId(groupId.getValue()))
                    .thenReturn(List.of(gathering));
            when(gatheringPort.findGatheringMemberDataByGatheringIds(anyList()))
                    .thenReturn(List.of());

            List<GatheringWithStatistics> result =
                    gatheringQueryService.getGatheringsWithStatisticsByGroupId(groupId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTotalWorshipAttendanceCount()).isEqualTo(0);
            assertThat(result.get(0).getTotalGatheringAttendanceCount()).isEqualTo(0);
            assertThat(result.get(0).getTotalPrayerRequestCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("모임이 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoGatherings() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            when(gatheringPort.findAllByGroupId(groupId.getValue())).thenReturn(List.of());
            when(gatheringPort.findGatheringMemberDataByGatheringIds(anyList()))
                    .thenReturn(List.of());

            List<GatheringWithStatistics> result =
                    gatheringQueryService.getGatheringsWithStatisticsByGroupId(groupId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("nth는 정렬 후 1부터 시작하는 인덱스")
        void shouldAssignNthBasedOnSortedPosition() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            GatheringId id1 = GatheringId.from(UUID.randomUUID());
            GatheringId id2 = GatheringId.from(UUID.randomUUID());

            Instant now = Instant.now();
            Gathering older = createGathering(id1, LocalDate.of(2025, 1, 1), now, List.of());
            Gathering newer = createGathering(id2, LocalDate.of(2025, 6, 1), now, List.of());

            when(gatheringPort.findAllByGroupId(groupId.getValue()))
                    .thenReturn(List.of(older, newer));
            when(gatheringPort.findGatheringMemberDataByGatheringIds(anyList()))
                    .thenReturn(List.of());

            List<GatheringWithStatistics> result =
                    gatheringQueryService.getGatheringsWithStatisticsByGroupId(groupId);

            assertThat(result.get(0).getNth()).isEqualTo(1);
            assertThat(result.get(0).getGathering().getDate()).isEqualTo(LocalDate.of(2025, 6, 1));
            assertThat(result.get(1).getNth()).isEqualTo(2);
            assertThat(result.get(1).getGathering().getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        }
    }
}
