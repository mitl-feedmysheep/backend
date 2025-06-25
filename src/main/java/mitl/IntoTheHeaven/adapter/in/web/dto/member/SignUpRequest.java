package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.Gender;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDate birthdate;
    private Gender gender;

    public SignUpCommand toCommand() {
        return SignUpCommand.builder()
                .username(username)
                .password(password) // In a real application, password should be encoded here or in the service layer.
                .name(name)
                .email(email)
                .birthdate(birthdate)
                .gender(gender)
                .build();
    }
} 