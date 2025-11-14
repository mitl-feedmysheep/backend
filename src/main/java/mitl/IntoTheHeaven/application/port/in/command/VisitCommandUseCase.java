package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;

public interface VisitCommandUseCase {

    // ADMIN - Create a new visit
    Visit createVisit(CreateVisitCommand command);

    // ADMIN - Update visit
    Visit updateVisit(VisitId visitId, UpdateVisitCommand command);

    // ADMIN - Delete visit (soft delete)
    void deleteVisit(VisitId visitId);

    // ADMIN - Add members to visit
    Visit addMembersToVisit(VisitId visitId, AddVisitMembersCommand command);

    // ADMIN - Remove member from visit
    Visit removeMemberFromVisit(VisitId visitId, VisitMemberId visitMemberId);
}
