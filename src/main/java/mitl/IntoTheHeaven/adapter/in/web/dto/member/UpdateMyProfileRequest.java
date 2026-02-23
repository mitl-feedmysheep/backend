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
    private String sex;

    private LocalDate birthday;

    private String phone;

    private String address;

    private String occupation;

    @Pattern(regexp = "BAPTIZED|NOT_BAPTIZED|PAEDOBAPTISM", message = "Invalid baptism status")
    private String baptismStatus;

    @Pattern(regexp = "[A-Z]{4}", message = "MBTI must be exactly 4 uppercase letters")
    private String mbti;

    public UpdateMyProfileCommand toCommand() {
        return UpdateMyProfileCommand.builder()
                .id(id != null ? MemberId.from(UUID.fromString(id)) : null)
                .name(name)
                .sex(sex)
                .birthday(birthday)
                .phone(phone)
                .address(address)
                .occupation(occupation)
                .baptismStatus(baptismStatus)
                .mbti(mbti)
                .build();
    }
}