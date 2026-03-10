package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateMyProfileCommand;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @Mock
    private MemberPort memberPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberCommandService memberCommandService;

    private UUID memberUuid;
    private MemberId memberId;
    private Member existingMember;

    @BeforeEach
    void setUp() {
        memberUuid = UUID.randomUUID();
        memberId = MemberId.from(memberUuid);
        existingMember = Member.builder()
                .id(memberId)
                .name("홍길동")
                .email("hong@test.com")
                .password("encodedPassword")
                .sex(Sex.M)
                .birthday(LocalDate.of(1990, 1, 1))
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .isProvisioned(false)
                .build();
    }

    @Nested
    @DisplayName("signUp - 회원 가입")
    class SignUpTests {

        @Test
        @DisplayName("비밀번호가 인코딩되고 isProvisioned가 false로 설정된다")
        void shouldEncodePasswordAndSetIsProvisionedFalse() {
            SignUpCommand command = SignUpCommand.builder()
                    .username("testuser")
                    .password("rawPassword")
                    .name("홍길동")
                    .email("hong@test.com")
                    .birthdate(LocalDate.of(1990, 1, 1))
                    .sex("M")
                    .phone("010-1234-5678")
                    .address("서울시 강남구")
                    .build();

            when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            Member result = memberCommandService.signUp(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();

            assertThat(saved.getPassword()).isEqualTo("encodedPassword");
            assertThat(saved.getIsProvisioned()).isFalse();
            assertThat(saved.getId()).isNotNull();
            verify(passwordEncoder).encode("rawPassword");
        }

        @Test
        @DisplayName("sex 문자열이 Sex enum으로 변환된다")
        void shouldConvertSexStringToEnum() {
            SignUpCommand command = SignUpCommand.builder()
                    .username("testuser")
                    .password("pw")
                    .name("김철수")
                    .email("kim@test.com")
                    .birthdate(LocalDate.of(1995, 6, 15))
                    .sex("F")
                    .phone("010-0000-0000")
                    .address("부산")
                    .build();

            when(passwordEncoder.encode(anyString())).thenReturn("enc");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.signUp(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            assertThat(captor.getValue().getSex()).isEqualTo(Sex.F);
        }

        @Test
        @DisplayName("모든 커맨드 필드가 Member에 올바르게 매핑된다")
        void shouldMapAllFieldsCorrectly() {
            SignUpCommand command = SignUpCommand.builder()
                    .username("testuser")
                    .password("pw")
                    .name("이영희")
                    .email("lee@test.com")
                    .birthdate(LocalDate.of(2000, 12, 25))
                    .sex("M")
                    .phone("010-9999-9999")
                    .address("대전시")
                    .build();

            when(passwordEncoder.encode(anyString())).thenReturn("enc");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.signUp(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("이영희");
            assertThat(saved.getEmail()).isEqualTo("lee@test.com");
            assertThat(saved.getBirthday()).isEqualTo(LocalDate.of(2000, 12, 25));
            assertThat(saved.getPhone()).isEqualTo("010-9999-9999");
            assertThat(saved.getAddress()).isEqualTo("대전시");
        }
    }

    @Nested
    @DisplayName("changePassword - 비밀번호 변경")
    class ChangePasswordTests {

        @Test
        @DisplayName("현재 비밀번호가 일치하면 새 비밀번호로 인코딩하여 저장하고 true를 반환한다")
        void shouldReturnTrueWhenCurrentPasswordMatches() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(passwordEncoder.matches("currentPw", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPw")).thenReturn("newEncodedPw");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            Boolean result = memberCommandService.changePassword(memberId, "currentPw", "newPw");

            assertThat(result).isTrue();
            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("newEncodedPw");
        }

        @Test
        @DisplayName("현재 비밀번호가 불일치하면 false를 반환한다")
        void shouldReturnFalseWhenCurrentPasswordDoesNotMatch() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(passwordEncoder.matches("wrongPw", "encodedPassword")).thenReturn(false);

            Boolean result = memberCommandService.changePassword(memberId, "wrongPw", "newPw");

            assertThat(result).isFalse();
            verify(memberPort, never()).save(any());
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenMemberNotFound() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberCommandService.changePassword(memberId, "pw", "newPw"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("changeEmail - 이메일 변경")
    class ChangeEmailTests {

        @Test
        @DisplayName("이메일을 변경하고 true를 반환한다")
        void shouldChangeEmailAndReturnTrue() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            Boolean result = memberCommandService.changeEmail(memberId, "new@test.com");

            assertThat(result).isTrue();
            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("new@test.com");
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenMemberNotFound() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberCommandService.changeEmail(memberId, "new@test.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }

    }

    @Nested
    @DisplayName("updateMyProfile - 프로필 업데이트")
    class UpdateMyProfileTests {

        @Test
        @DisplayName("모든 필드가 null이면 기존 값이 유지된다")
        void shouldKeepExistingValuesWhenAllFieldsNull() {
            UpdateMyProfileCommand command = UpdateMyProfileCommand.builder()
                    .id(memberId)
                    .build();

            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            Member result = memberCommandService.updateMyProfile(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("홍길동");
            assertThat(saved.getSex()).isEqualTo(Sex.M);
            assertThat(saved.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
            assertThat(saved.getPhone()).isEqualTo("010-1234-5678");
            assertThat(saved.getAddress()).isEqualTo("서울시 강남구");
        }

        @Test
        @DisplayName("일부 필드만 설정하면 해당 필드만 업데이트된다")
        void shouldUpdateOnlyNonNullFields() {
            UpdateMyProfileCommand command = UpdateMyProfileCommand.builder()
                    .id(memberId)
                    .name("김영희")
                    .mbti("INTJ")
                    .build();

            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.updateMyProfile(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("김영희");
            assertThat(saved.getMbti()).isEqualTo("INTJ");
            assertThat(saved.getSex()).isEqualTo(Sex.M);
            assertThat(saved.getPhone()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("모든 필드를 설정하면 전부 업데이트된다")
        void shouldUpdateAllFieldsWhenAllSet() {
            UpdateMyProfileCommand command = UpdateMyProfileCommand.builder()
                    .id(memberId)
                    .name("박지수")
                    .sex("F")
                    .birthday(LocalDate.of(1985, 3, 20))
                    .phone("010-5555-5555")
                    .address("인천시")
                    .occupation("엔지니어")
                    .baptismStatus("BAPTIZED")
                    .mbti("ENFP")
                    .build();

            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.updateMyProfile(command);

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();

            assertThat(saved.getName()).isEqualTo("박지수");
            assertThat(saved.getSex()).isEqualTo(Sex.F);
            assertThat(saved.getBirthday()).isEqualTo(LocalDate.of(1985, 3, 20));
            assertThat(saved.getPhone()).isEqualTo("010-5555-5555");
            assertThat(saved.getAddress()).isEqualTo("인천시");
            assertThat(saved.getOccupation()).isEqualTo("엔지니어");
            assertThat(saved.getBaptismStatus()).isEqualTo(BaptismStatus.BAPTIZED);
            assertThat(saved.getMbti()).isEqualTo("ENFP");
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenMemberNotFound() {
            UpdateMyProfileCommand command = UpdateMyProfileCommand.builder()
                    .id(memberId)
                    .build();

            when(memberPort.findById(memberUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberCommandService.updateMyProfile(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("resetPasswordByEmail - 이메일로 비밀번호 재설정")
    class ResetPasswordByEmailTests {

        @Test
        @DisplayName("이메일로 회원을 찾아 새 비밀번호를 인코딩하여 저장한다")
        void shouldResetPasswordSuccessfully() {
            when(memberPort.findByEmail("hong@test.com")).thenReturn(Optional.of(existingMember));
            when(passwordEncoder.encode("newPw")).thenReturn("newEncoded");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.resetPasswordByEmail("hong@test.com", "newPw");

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("newEncoded");
            verify(passwordEncoder).encode("newPw");
        }

        @Test
        @DisplayName("이메일로 회원을 찾지 못하면 RuntimeException이 발생한다")
        void shouldThrowWhenEmailNotFound() {
            when(memberPort.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberCommandService.resetPasswordByEmail("unknown@test.com", "pw"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("completeProvision - 프로비저닝 완료")
    class CompleteProvisionTests {

        @Test
        @DisplayName("이메일, 비밀번호를 변경하고 isProvisioned를 false로 설정한다")
        void shouldChangeEmailPasswordAndClearProvisionedFlag() {
            Member provisioned = existingMember.toBuilder().isProvisioned(true).build();
            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(provisioned));
            when(passwordEncoder.encode("newPw")).thenReturn("newEncodedPw");
            when(memberPort.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

            memberCommandService.completeProvision(memberId, "new@test.com", "newPw");

            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberPort).save(captor.capture());
            Member saved = captor.getValue();
            assertThat(saved.getEmail()).isEqualTo("new@test.com");
            assertThat(saved.getPassword()).isEqualTo("newEncodedPw");
            assertThat(saved.getIsProvisioned()).isFalse();
        }

        @Test
        @DisplayName("provisioned가 아닌 회원이면 RuntimeException이 발생한다")
        void shouldThrowWhenNotProvisioned() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.of(existingMember));

            assertThatThrownBy(() -> memberCommandService.completeProvision(memberId, "new@test.com", "newPw"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member is not provisioned");

            verify(memberPort, never()).save(any());
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenMemberNotFound() {
            when(memberPort.findById(memberUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberCommandService.completeProvision(memberId, "new@test.com", "newPw"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }
    }
}
