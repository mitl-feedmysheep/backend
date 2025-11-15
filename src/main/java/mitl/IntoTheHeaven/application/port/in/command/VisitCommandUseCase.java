package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitMemberCommand;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.domain.model.VisitMember;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;

public interface VisitCommandUseCase {

    // ADMIN - Create a new visit
    Visit createVisit(CreateVisitCommand command);

    // ADMIN - Update visit with church ownership verification
    Visit updateVisit(VisitId visitId, UpdateVisitCommand command, ChurchId churchId);

    // ADMIN - Delete visit (soft delete) with church ownership verification
    void deleteVisit(VisitId visitId, ChurchId churchId);

    // ADMIN - Add members to visit with church ownership verification
    Visit addMembersToVisit(VisitId visitId, AddVisitMembersCommand command, ChurchId churchId);

    // ADMIN - Remove member from visit with church ownership verification
    Visit removeMemberFromVisit(VisitId visitId, VisitMemberId visitMemberId, ChurchId churchId);

    // ADMIN - Update visit member story and prayers with church ownership
    // verification
    VisitMember updateVisitMember(UpdateVisitMemberCommand command, ChurchId churchId);
}
