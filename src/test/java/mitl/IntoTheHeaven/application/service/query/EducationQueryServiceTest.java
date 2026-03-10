package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.in.query.dto.EducationProgramWithProgress;
import mitl.IntoTheHeaven.application.port.out.EducationPort;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgramId;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgressId;
import mitl.IntoTheHeaven.domain.model.GatheringId;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationQueryServiceTest {

    @Mock
    private EducationPort educationPort;

    @Mock
    private GroupPort groupPort;

    @InjectMocks
    private EducationQueryService educationQueryService;

    private GroupMember createGroupMember(GroupMemberId gmId, GroupId groupId) {
        Member member = Member.builder()
                .id(MemberId.from(UUID.randomUUID()))
                .name("테스트멤버")
                .email("test@test.com")
                .password("encoded")
                .sex(Sex.M)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        return GroupMember.builder()
                .id(gmId)
                .groupId(groupId)
                .member(member)
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("getProgramWithProgress")
    class GetProgramWithProgress {

        @Test
        @DisplayName("교육 프로그램이 없으면 Optional.empty 반환")
        void shouldReturnEmptyWhenNoProgramExists() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            when(educationPort.findProgramByGroupId(groupId.getValue()))
                    .thenReturn(Optional.empty());

            Optional<EducationProgramWithProgress> result =
                    educationQueryService.getProgramWithProgress(groupId);

            assertThat(result).isEmpty();
            verify(groupPort, never()).findAllGroupMembersByGroupId(groupId.getValue());
        }

        @Test
        @DisplayName("교육 프로그램과 진도 정보를 함께 반환")
        void shouldReturnProgramWithProgress() {
            GroupId groupId = GroupId.from(UUID.randomUUID());
            GroupMemberId gm1Id = GroupMemberId.from(UUID.randomUUID());
            GroupMemberId gm2Id = GroupMemberId.from(UUID.randomUUID());

            EducationProgram program = EducationProgram.builder()
                    .id(EducationProgramId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .name("새신자 교육")
                    .description("12주 과정")
                    .totalWeeks(12)
                    .graduatedCount(3)
                    .build();

            GroupMember gm1 = createGroupMember(gm1Id, groupId);
            GroupMember gm2 = createGroupMember(gm2Id, groupId);

            EducationProgress progress1 = EducationProgress.builder()
                    .id(EducationProgressId.from(UUID.randomUUID()))
                    .groupMemberId(gm1Id)
                    .gatheringId(GatheringId.from(UUID.randomUUID()))
                    .weekNumber(1)
                    .completedDate(LocalDate.of(2025, 3, 1))
                    .build();

            when(educationPort.findProgramByGroupId(groupId.getValue()))
                    .thenReturn(Optional.of(program));
            when(groupPort.findAllGroupMembersByGroupId(groupId.getValue()))
                    .thenReturn(List.of(gm1, gm2));
            when(educationPort.findProgressByGroupMemberIds(List.of(gm1Id.getValue(), gm2Id.getValue())))
                    .thenReturn(List.of(progress1));

            Optional<EducationProgramWithProgress> result =
                    educationQueryService.getProgramWithProgress(groupId);

            assertThat(result).isPresent();
            assertThat(result.get().getProgram().getName()).isEqualTo("새신자 교육");
            assertThat(result.get().getProgram().getTotalWeeks()).isEqualTo(12);
            assertThat(result.get().getProgressList()).hasSize(1);
            assertThat(result.get().getProgressList().get(0).getWeekNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("그룹 멤버가 없으면 빈 진도 리스트 반환")
        void shouldReturnEmptyProgressWhenNoGroupMembers() {
            GroupId groupId = GroupId.from(UUID.randomUUID());

            EducationProgram program = EducationProgram.builder()
                    .id(EducationProgramId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .name("양육 과정")
                    .totalWeeks(8)
                    .graduatedCount(0)
                    .build();

            when(educationPort.findProgramByGroupId(groupId.getValue()))
                    .thenReturn(Optional.of(program));
            when(groupPort.findAllGroupMembersByGroupId(groupId.getValue()))
                    .thenReturn(List.of());

            Optional<EducationProgramWithProgress> result =
                    educationQueryService.getProgramWithProgress(groupId);

            assertThat(result).isPresent();
            assertThat(result.get().getProgressList()).isEmpty();
            verify(educationPort, never()).findProgressByGroupMemberIds(anyList());
        }
    }

    @Nested
    @DisplayName("getProgressByGathering")
    class GetProgressByGathering {

        @Test
        @DisplayName("모임별 교육 진도 조회")
        void shouldReturnProgressForGathering() {
            GatheringId gatheringId = GatheringId.from(UUID.randomUUID());

            List<EducationProgress> progressList = List.of(
                    EducationProgress.builder()
                            .id(EducationProgressId.from(UUID.randomUUID()))
                            .groupMemberId(GroupMemberId.from(UUID.randomUUID()))
                            .gatheringId(gatheringId)
                            .weekNumber(3)
                            .completedDate(LocalDate.of(2025, 3, 15))
                            .build()
            );

            when(educationPort.findProgressByGatheringId(gatheringId.getValue()))
                    .thenReturn(progressList);

            List<EducationProgress> result =
                    educationQueryService.getProgressByGathering(gatheringId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getWeekNumber()).isEqualTo(3);
            verify(educationPort).findProgressByGatheringId(gatheringId.getValue());
        }

        @Test
        @DisplayName("진도가 없으면 빈 리스트 반환")
        void shouldReturnEmptyListWhenNoProgress() {
            GatheringId gatheringId = GatheringId.from(UUID.randomUUID());

            when(educationPort.findProgressByGatheringId(gatheringId.getValue()))
                    .thenReturn(List.of());

            List<EducationProgress> result =
                    educationQueryService.getProgressByGathering(gatheringId);

            assertThat(result).isEmpty();
        }
    }
}
