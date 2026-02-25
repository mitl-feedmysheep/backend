package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.EducationCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GraduateMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.RecordEducationProgressCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.out.EducationPort;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgramId;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgressId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationCommandService implements EducationCommandUseCase {

    private final EducationPort educationPort;
    private final GroupPort groupPort;

    @Override
    public EducationProgram createProgram(CreateEducationProgramCommand command) {
        educationPort.findProgramByGroupId(command.getGroupId().getValue())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Education program already exists for group: " + command.getGroupId().getValue());
                });

        EducationProgram program = EducationProgram.builder()
                .id(EducationProgramId.from(UUID.randomUUID()))
                .groupId(command.getGroupId())
                .name(command.getName())
                .description(command.getDescription())
                .totalWeeks(command.getTotalWeeks())
                .graduatedCount(0)
                .build();

        return educationPort.saveProgram(program);
    }

    @Override
    public EducationProgram updateProgram(UpdateEducationProgramCommand command) {
        EducationProgram existing = educationPort.findProgramByGroupId(
                        command.getProgramId().getValue())
                .orElseThrow(() -> new RuntimeException(
                        "Education program not found: " + command.getProgramId().getValue()));

        EducationProgram updated = EducationProgram.builder()
                .id(existing.getId())
                .groupId(existing.getGroupId())
                .name(command.getName())
                .description(command.getDescription())
                .totalWeeks(command.getTotalWeeks())
                .graduatedCount(existing.getGraduatedCount())
                .build();

        return educationPort.updateProgram(updated);
    }

    @Override
    public EducationProgress recordProgress(RecordEducationProgressCommand command) {
        EducationProgress progress = EducationProgress.builder()
                .id(EducationProgressId.from(UUID.randomUUID()))
                .groupMemberId(command.getGroupMemberId())
                .gatheringId(command.getGatheringId())
                .weekNumber(command.getWeekNumber())
                .completedDate(LocalDate.now())
                .build();

        return educationPort.saveProgress(progress);
    }

    @Override
    public void removeProgress(EducationProgressId progressId) {
        educationPort.hardDeleteProgress(progressId.getValue());
    }

    @Override
    public void graduateMember(GraduateMemberCommand command) {
        EducationProgram program = educationPort.findProgramByGroupId(command.getGroupId().getValue())
                .orElseThrow(() -> new RuntimeException(
                        "Education program not found for group: " + command.getGroupId().getValue()));

        GroupMember groupMember = groupPort.findGroupMemberByGroupMemberId(
                command.getGroupMemberId().getValue());
        UUID memberId = groupMember.getMember().getId().getValue();

        educationPort.incrementGraduatedCount(program.getId().getValue());

        educationPort.graduateGroupMember(command.getGroupMemberId().getValue());

        educationPort.addGroupMember(command.getTargetGroupId().getValue(), memberId);
    }
}
