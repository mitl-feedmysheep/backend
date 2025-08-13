package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Birthdate is required")
    private LocalDate birthdate;
    
    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "M|F", message = "Sex must be either M or F")
    private String sex;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private String address;

    public SignUpCommand toCommand() {
        return SignUpCommand.builder()
                .password(password)
                .name(name)
                .email(email)
                .birthdate(birthdate)
                .sex(sex)
                .phone(phone)
                .address(address)
                .build();
    }
} 