package mitl.IntoTheHeaven.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 기도 알림에 노출할 수 있는 기도제목 (가벼운 조회용)
 *
 * @param personal      나의 기도 여부 (모임/심방과 무관)
 * @param gatheringDate 모임 기도제목의 모임 날짜 (나의 기도는 null)
 */
public record PrayerPushCandidate(
        UUID prayerId,
        UUID memberId,
        String prayerRequest,
        boolean personal,
        LocalDate gatheringDate,
        LocalDateTime createdAt
) {}
