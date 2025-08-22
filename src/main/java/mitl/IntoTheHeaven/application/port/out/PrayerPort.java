package mitl.IntoTheHeaven.application.port.out;

import java.util.List;
import java.util.UUID;

public interface PrayerPort {
    long findPrayerRequestCountByMemberIds(List<UUID> memberIds);
}
