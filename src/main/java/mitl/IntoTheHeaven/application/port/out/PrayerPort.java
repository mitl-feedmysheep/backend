package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Prayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrayerPort {
    long findPrayerRequestCountByMemberIds(List<UUID> memberIds);

    Long findPrayerRequestCountByChurchId(UUID churchId);

    Optional<Prayer> findById(UUID prayerId);

    Prayer save(Prayer prayer);
}
