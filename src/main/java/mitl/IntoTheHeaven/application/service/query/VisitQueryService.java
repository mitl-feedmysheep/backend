package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.VisitQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitQueryService implements VisitQueryUseCase {

    private final VisitPort visitPort;

    // ADMIN - Get all visits for a church
    @Override
    public List<Visit> getAllVisits(ChurchId churchId) {
        return visitPort.findAllByChurchId(churchId);
    }

    // ADMIN - Get visit by ID
    @Override
    public Visit getVisitById(VisitId visitId) {
        return visitPort.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));
    }

    // Get my visits (visits where I participated)
    @Override
    public List<Visit> getMyVisits(ChurchMemberId churchMemberId) {
        return visitPort.findMyVisits(churchMemberId);
    }
}

