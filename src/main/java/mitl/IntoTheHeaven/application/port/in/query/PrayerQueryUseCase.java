package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;

import java.util.List;

public interface PrayerQueryUseCase {
    Long getPrayerRequestCountByMemberIdAndChurchId(MemberId memberId, ChurchId churchId);

    List<Prayer> getMyPrayers(MemberId memberId);
}
