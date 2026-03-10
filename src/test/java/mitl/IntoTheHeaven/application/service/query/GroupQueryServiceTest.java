package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.dto.GroupWithLeader;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupQueryServiceTest {

    @Mock
    private GroupPort groupPort;

    @InjectMocks
    private GroupQueryService groupQueryService;

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

    @Nested
    @DisplayName("getGroupsByMemberId")
    class GetGroupsByMemberId {

        @Test
        @DisplayName("멤버 ID로 소속 그룹 목록 조회")
        void shouldDelegateToPort() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            List<Group> groups = List.of(
                    Group.builder()
                            .id(GroupId.from(UUID.randomUUID()))
                            .name("1셀")
                            .churchId(ChurchId.from(UUID.randomUUID()))
                            .build()
            );

            when(groupPort.findGroupsByMemberId(memberId.getValue())).thenReturn(groups);

            List<Group> result = groupQueryService.getGroupsByMemberId(memberId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("1셀");
            verify(groupPort).findGroupsByMemberId(memberId.getValue());
        }
    }

    @Nested
    @DisplayName("getGroupsByMemberIdAndChurchId")
    class GetGroupsByMemberIdAndChurchId {

        @Test
        @DisplayName("멤버 ID와 교회 ID로 그룹 목록 조회")
        void shouldDelegateToPort() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());
            List<Group> groups = List.of(
                    Group.builder()
                            .id(GroupId.from(UUID.randomUUID()))
                            .name("2셀")
                            .churchId(churchId)
                            .build()
            );

            when(groupPort.findGroupsByMemberIdAndChurchId(memberId.getValue(), churchId.getValue()))
                    .thenReturn(groups);

            List<Group> result = groupQueryService.getGroupsByMemberIdAndChurchId(memberId, churchId);

            assertThat(result).hasSize(1);
            verify(groupPort).findGroupsByMemberIdAndChurchId(memberId.getValue(), churchId.getValue());
        }
    }

    @Nested
    @DisplayName("getGroupMembersByGroupId")
    class GetGroupMembersByGroupId {

        @Test
        @DisplayName("역할 순서대로 정렬: LEADER → SUB_LEADER → MEMBER")
        void shouldSortByRolePriority() {
            UUID groupId = UUID.randomUUID();

            Member leaderMember = createMember("리더", LocalDate.of(1990, 3, 15));
            Member subLeaderMember = createMember("부리더", LocalDate.of(1992, 1, 10));
            Member regularMember = createMember("멤버", LocalDate.of(1988, 6, 20));

            GroupMember member = createGroupMember(GroupMemberRole.MEMBER, regularMember);
            GroupMember leader = createGroupMember(GroupMemberRole.LEADER, leaderMember);
            GroupMember subLeader = createGroupMember(GroupMemberRole.SUB_LEADER, subLeaderMember);

            when(groupPort.findGroupMembersByGroupId(groupId))
                    .thenReturn(List.of(member, leader, subLeader));

            List<GroupMember> result = groupQueryService.getGroupMembersByGroupId(groupId);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getRole()).isEqualTo(GroupMemberRole.LEADER);
            assertThat(result.get(1).getRole()).isEqualTo(GroupMemberRole.SUB_LEADER);
            assertThat(result.get(2).getRole()).isEqualTo(GroupMemberRole.MEMBER);
        }

        @Test
        @DisplayName("같은 역할 내에서 생년월일 오름차순 정렬 (나이 많은 순)")
        void shouldSortByBirthdayWithinSameRole() {
            UUID groupId = UUID.randomUUID();

            Member older = createMember("연장자", LocalDate.of(1985, 1, 1));
            Member middle = createMember("중간", LocalDate.of(1990, 6, 15));
            Member younger = createMember("막내", LocalDate.of(1995, 12, 31));

            GroupMember gm1 = createGroupMember(GroupMemberRole.MEMBER, younger);
            GroupMember gm2 = createGroupMember(GroupMemberRole.MEMBER, older);
            GroupMember gm3 = createGroupMember(GroupMemberRole.MEMBER, middle);

            when(groupPort.findGroupMembersByGroupId(groupId))
                    .thenReturn(List.of(gm1, gm2, gm3));

            List<GroupMember> result = groupQueryService.getGroupMembersByGroupId(groupId);

            assertThat(result.get(0).getMember().getName()).isEqualTo("연장자");
            assertThat(result.get(1).getMember().getName()).isEqualTo("중간");
            assertThat(result.get(2).getMember().getName()).isEqualTo("막내");
        }

        @Test
        @DisplayName("복합 정렬: 역할 우선, 동일 역할 내 생년월일 순")
        void shouldSortByRoleThenBirthday() {
            UUID groupId = UUID.randomUUID();

            Member leader = createMember("리더", LocalDate.of(1992, 5, 1));
            Member subLeaderOlder = createMember("부리더-선배", LocalDate.of(1985, 3, 10));
            Member subLeaderYounger = createMember("부리더-후배", LocalDate.of(1995, 8, 20));
            Member memberOldest = createMember("멤버-최연장", LocalDate.of(1980, 1, 1));
            Member memberYoungest = createMember("멤버-막내", LocalDate.of(2000, 12, 25));

            List<GroupMember> unsorted = List.of(
                    createGroupMember(GroupMemberRole.MEMBER, memberYoungest),
                    createGroupMember(GroupMemberRole.SUB_LEADER, subLeaderYounger),
                    createGroupMember(GroupMemberRole.LEADER, leader),
                    createGroupMember(GroupMemberRole.MEMBER, memberOldest),
                    createGroupMember(GroupMemberRole.SUB_LEADER, subLeaderOlder)
            );

            when(groupPort.findGroupMembersByGroupId(groupId)).thenReturn(unsorted);

            List<GroupMember> result = groupQueryService.getGroupMembersByGroupId(groupId);

            assertThat(result).hasSize(5);
            assertThat(result.get(0).getRole()).isEqualTo(GroupMemberRole.LEADER);
            assertThat(result.get(0).getMember().getName()).isEqualTo("리더");

            assertThat(result.get(1).getRole()).isEqualTo(GroupMemberRole.SUB_LEADER);
            assertThat(result.get(1).getMember().getName()).isEqualTo("부리더-선배");
            assertThat(result.get(2).getRole()).isEqualTo(GroupMemberRole.SUB_LEADER);
            assertThat(result.get(2).getMember().getName()).isEqualTo("부리더-후배");

            assertThat(result.get(3).getRole()).isEqualTo(GroupMemberRole.MEMBER);
            assertThat(result.get(3).getMember().getName()).isEqualTo("멤버-최연장");
            assertThat(result.get(4).getRole()).isEqualTo(GroupMemberRole.MEMBER);
            assertThat(result.get(4).getMember().getName()).isEqualTo("멤버-막내");
        }

        @Test
        @DisplayName("빈 그룹 멤버 목록 반환")
        void shouldReturnEmptyListWhenNoMembers() {
            UUID groupId = UUID.randomUUID();

            when(groupPort.findGroupMembersByGroupId(groupId)).thenReturn(List.of());

            List<GroupMember> result = groupQueryService.getGroupMembersByGroupId(groupId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getGroupMemberByGroupIdAndMemberId")
    class GetGroupMemberByGroupIdAndMemberId {

        @Test
        @DisplayName("그룹 ID와 멤버 ID로 그룹 멤버 조회")
        void shouldDelegateToPort() {
            GroupId groupId = GroupId.from(UUID.randomUUID());
            MemberId memberId = MemberId.from(UUID.randomUUID());
            Member member = createMember("멤버", LocalDate.of(1990, 1, 1));
            GroupMember groupMember = createGroupMember(GroupMemberRole.MEMBER, member);

            when(groupPort.findGroupMemberByGroupIdAndMemberId(groupId.getValue(), memberId.getValue()))
                    .thenReturn(groupMember);

            GroupMember result = groupQueryService.getGroupMemberByGroupIdAndMemberId(groupId, memberId);

            assertThat(result).isNotNull();
            verify(groupPort).findGroupMemberByGroupIdAndMemberId(groupId.getValue(), memberId.getValue());
        }
    }

    @Nested
    @DisplayName("getGroupsWithLeaderByChurchId")
    class GetGroupsWithLeaderByChurchId {

        @Test
        @DisplayName("교회 ID로 리더 포함 그룹 목록 조회")
        void shouldDelegateToPort() {
            ChurchId churchId = ChurchId.from(UUID.randomUUID());
            List<GroupWithLeader> groups = List.of(
                    GroupWithLeader.builder()
                            .groupId(UUID.randomUUID())
                            .groupName("1셀")
                            .leaderName("김리더")
                            .build()
            );

            when(groupPort.findGroupsWithLeaderByChurchId(churchId.getValue())).thenReturn(groups);

            List<GroupWithLeader> result = groupQueryService.getGroupsWithLeaderByChurchId(churchId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getGroupName()).isEqualTo("1셀");
            assertThat(result.get(0).getLeaderName()).isEqualTo("김리더");
        }
    }
}
