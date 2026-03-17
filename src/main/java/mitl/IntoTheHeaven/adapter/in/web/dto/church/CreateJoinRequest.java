package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateJoinRequest {
    private UUID departmentId;
}
