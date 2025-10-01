package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
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
     * Find visit by ID
     */
    Optional<Visit> findById(VisitId visitId);

    /**
     * Find all visits by church ID
     */
    List<Visit> findAllByChurchId(ChurchId churchId);

    /**
     * Delete visit (soft delete)
     */
    void delete(Visit visit);

    /**
     * Find all visits led by the pastor (church member)
     */
    List<Visit> findMyVisits(ChurchMemberId pastorChurchMemberId);
}

