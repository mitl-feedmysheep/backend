package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.PrayerId;

public interface PrayerCommandUseCase {
    void delete(PrayerId prayerId);

    void updateAnswered(PrayerId prayerId, boolean isAnswered);
}


