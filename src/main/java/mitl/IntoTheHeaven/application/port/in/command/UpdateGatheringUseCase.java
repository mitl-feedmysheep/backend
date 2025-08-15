package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringCommand;
import mitl.IntoTheHeaven.domain.model.Gathering;

public interface UpdateGatheringUseCase {
    Gathering updateGathering(UpdateGatheringCommand command);
}

