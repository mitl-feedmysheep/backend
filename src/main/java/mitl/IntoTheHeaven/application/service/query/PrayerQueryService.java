package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.in.query.PrayerQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrayerQueryService implements PrayerQueryUseCase {

    private final ChurchPort churchPort;
    private final PrayerPort prayerPort;

    @Override
    public Long getPrayerRequestCountByMemberIdAndChurchId(MemberId memberId, ChurchId churchId) {

        // 1. Check if the user belongs to the church using churchPort
        Church church = churchPort.findById(churchId.getValue());
        if (church == null) {
            throw new RuntimeException("권한이 없어요 :(");
        }

        // 2. Get the count of prayer requests for the church
        // This counts only prayers that originated from gatherings within the church
        return prayerPort.findPrayerRequestCountByChurchId(churchId.getValue());
    }
}
