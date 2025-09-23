package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateMyProfileCommand;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMyProfileRequest {

    private String id; // must match principal (UUID string)

    private String name;

    @Pattern(regexp = "M|F", message = "Sex must be either M or F")
    private String sex; // optional

    private LocalDate birthday; // optional

    private String phone; // optional

    public UpdateMyProfileCommand toCommand() {
        return UpdateMyProfileCommand.builder()
                .id(id != null ? MemberId.from(UUID.fromString(id)) : null)
                .name(name)
                .sex(sex)
                .birthday(birthday)
                .phone(phone)
                .build();
    }
}