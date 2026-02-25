package mitl.IntoTheHeaven.adapter.in.web.dto.education;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.EducationProgress;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class EducationProgressResponse {

    private final UUID id;
    private final UUID groupMemberId;
    private final UUID gatheringId;
    private final int weekNumber;
    private final LocalDate completedDate;

    public static EducationProgressResponse from(EducationProgress progress) {
        return EducationProgressResponse.builder()
                .id(progress.getId().getValue())
                .groupMemberId(progress.getGroupMemberId().getValue())
                .gatheringId(progress.getGatheringId().getValue())
                .weekNumber(progress.getWeekNumber())
                .completedDate(progress.getCompletedDate())
                .build();
    }

    public static List<EducationProgressResponse> from(List<EducationProgress> progressList) {
        return progressList.stream()
                .map(EducationProgressResponse::from)
                .toList();
    }
}
