package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface ChurchQueryUseCase {
    List<Church> getChurchesByMemberId(MemberId memberId);
}