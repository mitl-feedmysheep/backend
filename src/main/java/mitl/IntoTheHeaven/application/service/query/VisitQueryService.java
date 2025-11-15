package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.VisitQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.VisitPort;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitQueryService implements VisitQueryUseCase {

        private final VisitPort visitPort;

        // ADMIN - Get all my visits for a church
        @Override
        public List<Visit> getAllMyVisits(ChurchId churchId, MemberId memberId) {
                List<Visit> visits = visitPort.findAllByChurchIdAndMemberId(churchId, memberId);

                // Sort by date desc, then startedAt desc
                return visits.stream()
                                .sorted(Comparator.comparing(Visit::getDate).reversed()
                                                .thenComparing(Comparator.comparing(Visit::getStartedAt).reversed()))
                                .collect(Collectors.toList());
        }

        // ADMIN - Get visit by ID with church ownership verification
        @Override
        public Visit getVisitById(VisitId visitId, ChurchId churchId) {
                Visit visit = visitPort.findDetailById(visitId)
                                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));

                // Verify church ownership
                if (!visit.getChurchId().equals(churchId)) {
                        throw new IllegalArgumentException("Access denied: Visit does not belong to your church");
                }

                return visit;
        }

        // Get my visits (visits where I participated)
        @Override
        public List<Visit> getMyVisits(ChurchMemberId churchMemberId) {
                List<Visit> visits = visitPort.findMyVisits(churchMemberId);

                // Sort by date desc, then startedAt desc
                return visits.stream()
                                .sorted(Comparator.comparing(Visit::getDate).reversed()
                                                .thenComparing(Comparator.comparing(Visit::getStartedAt).reversed()))
                                .collect(Collectors.toList());
        }
}
