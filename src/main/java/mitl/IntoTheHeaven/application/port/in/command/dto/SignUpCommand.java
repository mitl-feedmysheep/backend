package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCommand {

    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDate birthdate;
    private String sex;
    private String phone;
    private String address;
    private String description;
} 