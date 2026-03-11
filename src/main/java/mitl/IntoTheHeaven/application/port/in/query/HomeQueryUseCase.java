package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.adapter.in.web.dto.home.HomeSummaryResponse;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface HomeQueryUseCase {
    HomeSummaryResponse getHomeSummary(MemberId memberId);
}
