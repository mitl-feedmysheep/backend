package mitl.IntoTheHeaven.adapter.in.web.dto.home;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeGoalResponse {
    private final String groupName;
    private final String goal;
}
