package mitl.IntoTheHeaven.adapter.in.web.dto.church;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;

// ADMIN - Request DTO for selecting a church context
@Getter
public class AdminSelectChurchRequest {

  @NotNull(message = "churchId is required")
  private UUID churchId;
}
