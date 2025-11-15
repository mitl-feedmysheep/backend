package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;

import java.util.List;
import java.util.Optional;

public interface VisitPort {

    /**
     * Save visit
     */
    Visit save(Visit visit);

    /**
     * Find visit by ID with detailed information (visit members and prayers)
     */
    Optional<Visit> findDetailById(VisitId visitId);

    /**
     * Find all visits by church ID
     */
    List<Visit> findAllByChurchIdAndMemberId(ChurchId churchId, MemberId memberId);

    /**
     * Delete visit (soft delete)
     */
    void delete(Visit visit);

    /**
     * Find all visits led by the pastor (church member)
     */
    List<Visit> findMyVisits(ChurchMemberId pastorChurchMemberId);
}
