package mitl.IntoTheHeaven.adapter.in.web.dto.home;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomePrayerResponse {
    private final String groupName;
    private final String prayerRequest;
    private final String description;
    private final boolean isAnswered;
}
