package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
import mitl.IntoTheHeaven.domain.model.Gathering;

public interface GatheringCommandUseCase {
    Gathering createGathering(CreateGatheringCommand command);
} 