package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateMyProfileCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
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
                                .id(MemberId.from(UUID.randomUUID()))
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

                Member updated = member.toBuilder()
                                .password(passwordEncoder.encode(newPassword))
                                .build();

                memberPort.save(updated);
                return true;
        }

        @Override
        public Boolean changeEmail(MemberId memberId, String newEmail) {
                Member member = memberPort.findById(memberId.getValue())
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                Member updated = member.toBuilder()
                                .email(newEmail)
                                .build();

                memberPort.save(updated);
                return true;
        }

        @Override
        public Member updateMyProfile(UpdateMyProfileCommand command) {
                Member member = memberPort.findById(command.getId().getValue())
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                Member.MemberBuilder<?, ?> builder = member.toBuilder();

                if (command.getName() != null) builder.name(command.getName());
                if (command.getSex() != null) builder.sex(Sex.valueOf(command.getSex()));
                if (command.getBirthday() != null) builder.birthday(command.getBirthday());
                if (command.getPhone() != null) builder.phone(command.getPhone());
                if (command.getAddress() != null) builder.address(command.getAddress());
                if (command.getOccupation() != null) builder.occupation(command.getOccupation());
                if (command.getBaptismStatus() != null) builder.baptismStatus(BaptismStatus.valueOf(command.getBaptismStatus()));
                if (command.getMbti() != null) builder.mbti(command.getMbti());

                return memberPort.save(builder.build());
        }

        @Override
        public void resetPasswordByEmail(String email, String newPassword) {
                Member member = memberPort.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                Member updated = member.toBuilder()
                                .password(passwordEncoder.encode(newPassword))
                                .build();

                memberPort.save(updated);
        }
}
