package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GraduateMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.RecordEducationProgressCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.out.EducationPort;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationCommandServiceTest {

    @Mock
    private EducationPort educationPort;

    @Mock
    private GroupPort groupPort;

    @InjectMocks
    private EducationCommandService educationCommandService;

    private UUID groupUuid;
    private GroupId groupId;

    @BeforeEach
    void setUp() {
        groupUuid = UUID.randomUUID();
        groupId = GroupId.from(groupUuid);
    }

    @Nested
    @DisplayName("createProgram - 교육 프로그램 생성")
    class CreateProgramTests {

        @Test
        @DisplayName("교육 프로그램이 정상적으로 생성된다")
        void shouldCreateProgramSuccessfully() {
            CreateEducationProgramCommand command = new CreateEducationProgramCommand(
                    groupId, "바이블 스터디", "성경 공부 커리큘럼", 12);

            when(educationPort.findProgramByGroupId(groupUuid)).thenReturn(Optional.empty());
            when(educationPort.saveProgram(any(EducationProgram.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            EducationProgram result = educationCommandService.createProgram(command);

            ArgumentCaptor<EducationProgram> captor = ArgumentCaptor.forClass(EducationProgram.class);
            verify(educationPort).saveProgram(captor.capture());
            EducationProgram saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getGroupId()).isEqualTo(groupId);
            assertThat(saved.getName()).isEqualTo("바이블 스터디");
            assertThat(saved.getDescription()).isEqualTo("성경 공부 커리큘럼");
            assertThat(saved.getTotalWeeks()).isEqualTo(12);
            assertThat(saved.getGraduatedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("그룹에 이미 프로그램이 있으면 IllegalStateException이 발생한다")
        void shouldThrowWhenProgramAlreadyExists() {
            EducationProgram existing = EducationProgram.builder()
                    .id(EducationProgramId.from(UUID.randomUUID()))
                    .groupId(groupId)
                    .name("기존 프로그램")
                    .build();

            CreateEducationProgramCommand command = new CreateEducationProgramCommand(
                    groupId, "새 프로그램", "설명", 8);

            when(educationPort.findProgramByGroupId(groupUuid)).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> educationCommandService.createProgram(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already exists");

            verify(educationPort, never()).saveProgram(any());
        }
    }

    @Nested
    @DisplayName("updateProgram - 교육 프로그램 업데이트")
    class UpdateProgramTests {

        @Test
        @DisplayName("기존 id, groupId, graduatedCount를 보존하면서 업데이트한다")
        void shouldPreserveIdAndGroupIdAndGraduatedCount() {
            EducationProgramId programId = EducationProgramId.from(UUID.randomUUID());

            EducationProgram existing = EducationProgram.builder()
                    .id(programId)
                    .groupId(groupId)
                    .name("기존 이름")
                    .description("기존 설명")
                    .totalWeeks(10)
                    .graduatedCount(5)
                    .build();

            UpdateEducationProgramCommand command = new UpdateEducationProgramCommand(
                    programId, "새 이름", "새 설명", 20);

            when(educationPort.findProgramByGroupId(programId.getValue()))
                    .thenReturn(Optional.of(existing));
            when(educationPort.updateProgram(any(EducationProgram.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            EducationProgram result = educationCommandService.updateProgram(command);

            ArgumentCaptor<EducationProgram> captor = ArgumentCaptor.forClass(EducationProgram.class);
            verify(educationPort).updateProgram(captor.capture());
            EducationProgram saved = captor.getValue();

            assertThat(saved.getId()).isEqualTo(programId);
            assertThat(saved.getGroupId()).isEqualTo(groupId);
            assertThat(saved.getGraduatedCount()).isEqualTo(5);
            assertThat(saved.getName()).isEqualTo("새 이름");
            assertThat(saved.getDescription()).isEqualTo("새 설명");
            assertThat(saved.getTotalWeeks()).isEqualTo(20);
        }

        @Test
        @DisplayName("프로그램이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenProgramNotFound() {
            EducationProgramId programId = EducationProgramId.from(UUID.randomUUID());
            UpdateEducationProgramCommand command = new UpdateEducationProgramCommand(
                    programId, "이름", "설명", 10);

            when(educationPort.findProgramByGroupId(programId.getValue()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> educationCommandService.updateProgram(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Education program not found");
        }
    }

    @Nested
    @DisplayName("recordProgress - 교육 진도 기록")
    class RecordProgressTests {

        @Test
        @DisplayName("completedDate가 LocalDate.now()로 설정된다")
        void shouldSetCompletedDateToToday() {
            GroupMemberId gmId = GroupMemberId.from(UUID.randomUUID());
            GatheringId gId = GatheringId.from(UUID.randomUUID());

            RecordEducationProgressCommand command = new RecordEducationProgressCommand(
                    gId, gmId, 3);

            when(educationPort.saveProgress(any(EducationProgress.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            EducationProgress result = educationCommandService.recordProgress(command);

            ArgumentCaptor<EducationProgress> captor = ArgumentCaptor.forClass(EducationProgress.class);
            verify(educationPort).saveProgress(captor.capture());
            EducationProgress saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getGroupMemberId()).isEqualTo(gmId);
            assertThat(saved.getGatheringId()).isEqualTo(gId);
            assertThat(saved.getWeekNumber()).isEqualTo(3);
            assertThat(saved.getCompletedDate()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("removeProgress - 교육 진도 삭제 (hard delete)")
    class RemoveProgressTests {

        @Test
        @DisplayName("hard delete를 수행한다")
        void shouldHardDeleteProgress() {
            UUID progressUuid = UUID.randomUUID();
            EducationProgressId progressId = EducationProgressId.from(progressUuid);

            educationCommandService.removeProgress(progressId);

            verify(educationPort).hardDeleteProgress(progressUuid);
        }
    }

    @Nested
    @DisplayName("graduateMember - 멤버 졸업 처리")
    class GraduateMemberTests {

        @Test
        @DisplayName("3단계 졸업 프로세스를 수행한다")
        void shouldExecuteThreeStepGraduationProcess() {
            UUID programUuid = UUID.randomUUID();
            GroupMemberId gmId = GroupMemberId.from(UUID.randomUUID());
            GroupId targetGroupId = GroupId.from(UUID.randomUUID());
            UUID memberUuid = UUID.randomUUID();

            EducationProgram program = EducationProgram.builder()
                    .id(EducationProgramId.from(programUuid))
                    .groupId(groupId)
                    .name("프로그램")
                    .graduatedCount(3)
                    .build();

            GroupMember gm = GroupMember.builder()
                    .id(gmId)
                    .groupId(groupId)
                    .member(Member.builder().id(MemberId.from(memberUuid)).name("테스트").build())
                    .build();

            GraduateMemberCommand command = new GraduateMemberCommand(
                    groupId, gmId, targetGroupId);

            when(educationPort.findProgramByGroupId(groupUuid)).thenReturn(Optional.of(program));
            when(groupPort.findGroupMemberByGroupMemberId(gmId.getValue())).thenReturn(gm);

            educationCommandService.graduateMember(command);

            verify(educationPort).incrementGraduatedCount(programUuid);
            verify(educationPort).graduateGroupMember(gmId.getValue());
            verify(educationPort).addGroupMember(targetGroupId.getValue(), memberUuid);
        }

        @Test
        @DisplayName("프로그램이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenProgramNotFound() {
            GroupMemberId gmId = GroupMemberId.from(UUID.randomUUID());
            GroupId targetGroupId = GroupId.from(UUID.randomUUID());

            GraduateMemberCommand command = new GraduateMemberCommand(
                    groupId, gmId, targetGroupId);

            when(educationPort.findProgramByGroupId(groupUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> educationCommandService.graduateMember(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Education program not found");
        }
    }
}
