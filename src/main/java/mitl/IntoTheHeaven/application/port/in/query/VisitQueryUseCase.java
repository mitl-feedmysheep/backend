package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;

import java.util.List;

public interface VisitQueryUseCase {

    // ADMIN - Get all my visits for a church
    List<Visit> getAllMyVisits(ChurchId churchId, MemberId memberId);

    // ADMIN - Get visit by ID with church ownership verification
    Visit getVisitById(VisitId visitId, ChurchId churchId);

    // Get my visits (visits where I participated)
    List<Visit> getMyVisits(ChurchMemberId churchMemberId);
}
