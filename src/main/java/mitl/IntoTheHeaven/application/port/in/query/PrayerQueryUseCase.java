package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface PrayerQueryUseCase {
    Long getPrayerRequestCountByMemberIdAndChurchId(MemberId memberId, ChurchId churchId);
}
