package mitl.IntoTheHeaven.adapter.in.web.dto.education;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.application.port.in.query.dto.EducationProgramWithProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class EducationProgramResponse {

    private final UUID id;
    private final UUID groupId;
    private final String name;
    private final String description;
    private final int totalWeeks;
    private final int graduatedCount;
    private final List<MemberProgressSummary> memberProgress;

    @Getter
    @Builder
    public static class MemberProgressSummary {
        private final UUID groupMemberId;
        private final List<Integer> completedWeeks;
        private final int completedCount;
    }

    public static EducationProgramResponse from(EducationProgramWithProgress data) {
        EducationProgram program = data.getProgram();

        Map<UUID, List<EducationProgress>> progressByMember = data.getProgressList().stream()
                .collect(Collectors.groupingBy(p -> p.getGroupMemberId().getValue()));

        List<MemberProgressSummary> summaries = progressByMember.entrySet().stream()
                .map(entry -> {
                    List<Integer> weeks = entry.getValue().stream()
                            .map(EducationProgress::getWeekNumber)
                            .sorted()
                            .toList();
                    return MemberProgressSummary.builder()
                            .groupMemberId(entry.getKey())
                            .completedWeeks(weeks)
                            .completedCount(weeks.size())
                            .build();
                })
                .toList();

        return EducationProgramResponse.builder()
                .id(program.getId().getValue())
                .groupId(program.getGroupId().getValue())
                .name(program.getName())
                .description(program.getDescription())
                .totalWeeks(program.getTotalWeeks())
                .graduatedCount(program.getGraduatedCount())
                .memberProgress(summaries)
                .build();
    }
}
