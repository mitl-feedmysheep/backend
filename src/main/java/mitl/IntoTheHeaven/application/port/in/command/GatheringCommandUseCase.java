package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringCommand;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringMember;

public interface GatheringCommandUseCase {
    Gathering createGathering(CreateGatheringCommand command);
    GatheringMember updateGatheringMember(UpdateGatheringMemberCommand command);
    Gathering updateGathering(UpdateGatheringCommand command);
} 