package mitl.IntoTheHeaven.adapter.in.web.dto.push;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnsubscribePushRequest {

    @NotBlank
    private String endpoint;
}
