package mitl.IntoTheHeaven.adapter.in.web.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadCountResponse {
    private final long count;
}
