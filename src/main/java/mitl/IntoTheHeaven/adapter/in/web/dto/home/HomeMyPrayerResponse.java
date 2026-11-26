package mitl.IntoTheHeaven.adapter.in.web.dto.home;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/** 홈 "나의 기도" 섹션 항목 (응답 체크 전인 나의 기도) */
@Getter
@Builder
public class HomeMyPrayerResponse {
    private final UUID id;
    private final String prayerRequest;
    private final LocalDateTime createdAt;
}
