package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateMyProfileCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase {

    private final MemberPort memberPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member signUp(SignUpCommand command) {
        Member member = Member.builder()
                .id(MemberId.from(UUID.randomUUID())) // Generate a new UUID for the member
                .name(command.getName())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .sex(Sex.valueOf(command.getSex()))
                .birthday(command.getBirthdate())
                .phone(command.getPhone())
                .address(command.getAddress())
                .isProvisioned(false)
                .build();

        return memberPort.save(member);
    }

    @Override
    public Boolean changePassword(MemberId memberId, String currentPassword, String newPassword) {
        Member member = memberPort.findById(memberId.getValue())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }

        Member updated = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(passwordEncoder.encode(newPassword))
                .sex(member.getSex())
                .birthday(member.getBirthday())
                .phone(member.getPhone())
                .profileUrl(member.getProfileUrl())
                .address(member.getAddress())
                .isProvisioned(member.getIsProvisioned())
                .build();

        memberPort.save(updated);
        
        return true;
    }

    @Override
    public Boolean changeEmail(MemberId memberId, String newEmail) {
        Member member = memberPort.findById(memberId.getValue())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Member updated = Member.builder()
                .id(member.getId())
                .name(member.getName())
                .email(newEmail)
                .password(member.getPassword())
                .sex(member.getSex())
                .birthday(member.getBirthday())
                .phone(member.getPhone())
                .profileUrl(member.getProfileUrl())
                .address(member.getAddress())
                .isProvisioned(false)
                .build();

        memberPort.save(updated);

        return true;
    }

    @Override
    public Member updateMyProfile(UpdateMyProfileCommand command) {
        Member member = memberPort.findById(command.getId().getValue())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Member updated = Member.builder()
                .id(member.getId())
                .name(command.getName() != null ? command.getName() : member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .sex(command.getSex() != null ? Sex.valueOf(command.getSex()) : member.getSex())
                .birthday(command.getBirthday() != null ? command.getBirthday() : member.getBirthday())
                .phone(command.getPhone() != null ? command.getPhone() : member.getPhone())
                .profileUrl(member.getProfileUrl())
                .address(member.getAddress())
                .isProvisioned(member.getIsProvisioned())
                .build();

        return memberPort.save(updated);
    }
} 