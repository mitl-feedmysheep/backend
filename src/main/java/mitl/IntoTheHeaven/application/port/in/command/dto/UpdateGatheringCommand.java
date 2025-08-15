package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringRequest;
import mitl.IntoTheHeaven.domain.model.GatheringId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateGatheringCommand {

    private GatheringId gatheringId;
    private String name;
    private String description;
    private LocalDate date;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String place;
    private String leaderComment;
    private String adminComment;

    public static UpdateGatheringCommand from(GatheringId gatheringId, UpdateGatheringRequest request) {
        return new UpdateGatheringCommand(
                gatheringId,
                request.getName(),
                request.getDescription(),
                request.getDate(),
                request.getStartedAt() != null ? request.getStartedAt().toLocalDateTime() : null,
                request.getEndedAt() != null ? request.getEndedAt().toLocalDateTime() : null,
                request.getPlace(),
                request.getLeaderComment(),
                request.getAdminComment()
        );
    }
}

