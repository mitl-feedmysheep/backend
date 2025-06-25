package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase {

    private final MemberPort memberPort;

    @Override
    public Member signUp(SignUpCommand command) {
        // In a real application, you should check for duplicate username/email here.

        Member newMember = Member.builder()
                .id(UUID.randomUUID()) // Generate a new UUID for the member
                .name(command.getName())
                .email(command.getEmail())
                // Passwords should be encoded using a password encoder like BCryptPasswordEncoder
                .password(command.getPassword())
                .sex(command.getGender())
                .birthday(command.getBirthdate())
                .build();

        return memberPort.save(newMember);
    }
} 