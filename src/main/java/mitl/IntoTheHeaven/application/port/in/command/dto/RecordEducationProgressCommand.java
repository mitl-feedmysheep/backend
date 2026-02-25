package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;

@Getter
@RequiredArgsConstructor
public class RecordEducationProgressCommand {

    private final GatheringId gatheringId;
    private final GroupMemberId groupMemberId;
    private final int weekNumber;
}
