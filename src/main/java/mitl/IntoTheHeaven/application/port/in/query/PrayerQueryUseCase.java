package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface PrayerQueryUseCase {
    Integer getPrayerRequestCountByMemberIdAndChurchId(MemberId memberId, ChurchId churchId);
}
