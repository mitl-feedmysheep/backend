package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PrayerCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PrayerCommandService implements PrayerCommandUseCase {

    private final PrayerPort prayerPort;

    @Override
    public PrayerId createPersonal(MemberId memberId, String prayerRequest) {
        Prayer saved = prayerPort.save(Prayer.createPersonal(memberId, prayerRequest));
        return saved.getId();
    }

    @Override
    public void updateContent(MemberId callerId, PrayerId prayerId, String prayerRequest) {
        Prayer prayer = getPrayer(prayerId);
        if (!prayer.isPersonal()) {
            throw new IllegalArgumentException("Only personal prayers can be edited here");
        }
        requireOwner(prayer, callerId);
        prayerPort.save(prayer.updateContent(prayerRequest));
    }

    @Override
    public void delete(MemberId callerId, PrayerId prayerId) {
        Prayer prayer = getPrayer(prayerId);
        // 모임 기도제목은 리더/부리더가 모임 화면에서 삭제하므로 기존 동작 유지, 나의 기도는 본인만
        if (prayer.isPersonal()) {
            requireOwner(prayer, callerId);
        }
        prayerPort.save(prayer.delete());
    }

    @Override
    public void updateAnswered(MemberId callerId, PrayerId prayerId, boolean isAnswered) {
        Prayer prayer = getPrayer(prayerId);
        requireOwner(prayer, callerId);
        prayerPort.save(prayer.markAnswered(isAnswered));
    }

    private Prayer getPrayer(PrayerId prayerId) {
        return prayerPort.findById(prayerId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Prayer not found"));
    }

    private void requireOwner(Prayer prayer, MemberId callerId) {
        if (!prayer.isOwnedBy(callerId)) {
            throw new AccessDeniedException("Not the owner of this prayer");
        }
    }
}
