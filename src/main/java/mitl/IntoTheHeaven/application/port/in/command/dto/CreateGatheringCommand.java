package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.CreateGatheringRequest;
import mitl.IntoTheHeaven.domain.model.GroupId;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateGatheringCommand {

    private GroupId groupId;
    private String name;
    private String description;
    private LocalDate date;
    private Instant startedAt;
    private Instant endedAt;
    private String place;

    public static CreateGatheringCommand from(CreateGatheringRequest request) {
        return new CreateGatheringCommand(
                GroupId.from(request.getGroupId()),
                request.getName(),
                request.getDescription(),
                request.getDate(),
                request.getStartedAt().toInstant(),
                request.getEndedAt().toInstant(),
                request.getPlace()
        );
    }
} 