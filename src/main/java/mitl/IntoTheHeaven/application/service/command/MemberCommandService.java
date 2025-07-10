package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase {

    private final MemberPort memberPort;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Member signUp(SignUpCommand command) {
        Member member = Member.builder()
                .id(MemberId.from(UUID.randomUUID())) // Generate a new UUID for the member
                .name(command.getName())
                .email(command.getEmail())
                // Passwords should be encoded using a password encoder like BCryptPasswordEncoder
                .password(passwordEncoder.encode(command.getPassword()))
                .sex(command.getGender())
                .birthday(command.getBirthdate())
                .phone(command.getPhone())
                .address(command.getAddress())
                .description(command.getDescription())
                .build();

        return memberPort.save(member);
    }
} 