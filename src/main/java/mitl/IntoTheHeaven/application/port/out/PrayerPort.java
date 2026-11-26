package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Prayer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrayerPort {
    long findPrayerRequestCountByMemberIds(List<UUID> memberIds);

    Long findPrayerRequestCountByChurchId(UUID churchId);

    Optional<Prayer> findById(UUID prayerId);

    Prayer save(Prayer prayer);

    List<Prayer> findAllByMemberId(UUID memberId);

    List<Prayer> findByGatheringMemberIds(List<UUID> gatheringMemberIds);

    /**
     * 기도 알림 후보: 미응답, 심방 기도 제외. 나의 기도 전체 + 모임 날짜가 oldestGatheringDate 이후인 모임 기도제목.
     */
    List<PrayerPushCandidate> findUnansweredForPush(List<UUID> memberIds, LocalDate oldestGatheringDate);
}