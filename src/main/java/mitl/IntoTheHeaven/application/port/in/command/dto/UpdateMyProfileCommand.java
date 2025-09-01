package mitl.IntoTheHeaven.application.port.in.command.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.model.MemberId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMyProfileCommand {

    private MemberId id;
    private String name;
    private String sex; // "M" or "F"
    private LocalDate birthday;
    private String phone;
}


