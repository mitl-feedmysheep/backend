package mitl.IntoTheHeaven.adapter.in.web.dto.home;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeSummaryResponse {
    private final List<HomeGoalResponse> goals;
    private final List<HomePrayerResponse> prayers;
}
