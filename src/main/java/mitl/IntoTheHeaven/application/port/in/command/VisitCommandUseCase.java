package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;

public interface VisitCommandUseCase {

    // ADMIN - Create a new visit
    Visit createVisit(CreateVisitCommand command);

    // ADMIN - Update visit
    Visit updateVisit(VisitId visitId, UpdateVisitCommand command);

    // ADMIN - Delete visit (soft delete)
    void deleteVisit(VisitId visitId);
}

