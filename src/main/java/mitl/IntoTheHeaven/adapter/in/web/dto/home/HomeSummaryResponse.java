package mitl.IntoTheHeaven.adapter.in.web.dto.home;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeSummaryResponse {
    private final List<HomeGoalResponse> goals;
    private final List<HomePrayerResponse> prayers;
    /** 나의 기도 (미응답, 최근순 최대 HOME_MY_PRAYER_LIMIT개) */
    private final List<HomeMyPrayerResponse> myPrayers;
    /** 미응답 나의 기도 전체 개수 ("전체보기" 판단용) */
    private final int myPrayerCount;
}
