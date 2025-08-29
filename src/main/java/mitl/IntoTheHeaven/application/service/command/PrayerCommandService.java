package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PrayerCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PrayerCommandService implements PrayerCommandUseCase {

    private final PrayerPort prayerPort;

    @Override
    public void delete(PrayerId prayerId) {
        Prayer prayer = prayerPort.findById(prayerId.getValue())
                .orElseThrow(() -> new RuntimeException("Prayer not found"));
        Prayer deleted = prayer.delete();
        prayerPort.save(deleted);
    }
}


