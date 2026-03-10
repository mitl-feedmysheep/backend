package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberPort memberPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("이메일로 사용자 조회 성공 - UserDetails의 username은 memberId UUID 문자열")
        void shouldReturnUserDetailsWithMemberIdAsUsername() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            String email = "user@test.com";

            Member member = Member.builder()
                    .id(memberId)
                    .name("테스트유저")
                    .email(email)
                    .password("$2a$10$encodedpassword")
                    .sex(Sex.M)
                    .birthday(LocalDate.of(1990, 5, 15))
                    .build();

            when(memberPort.findByEmail(email)).thenReturn(Optional.of(member));

            UserDetails result = customUserDetailsService.loadUserByUsername(email);

            assertThat(result.getUsername()).isEqualTo(memberUuid.toString());
            assertThat(result.getPassword()).isEqualTo("$2a$10$encodedpassword");
            verify(memberPort).findByEmail(email);
        }

        @Test
        @DisplayName("이메일로 사용자 조회 성공 - 권한이 USER로 설정됨")
        void shouldHaveUserAuthority() {
            UUID memberUuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(memberUuid);
            String email = "admin@test.com";

            Member member = Member.builder()
                    .id(memberId)
                    .name("관리자")
                    .email(email)
                    .password("encoded")
                    .sex(Sex.F)
                    .birthday(LocalDate.of(1985, 12, 25))
                    .build();

            when(memberPort.findByEmail(email)).thenReturn(Optional.of(member));

            UserDetails result = customUserDetailsService.loadUserByUsername(email);

            assertThat(result.getAuthorities()).hasSize(1);
            assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("USER");
        }

        @Test
        @DisplayName("존재하지 않는 이메일 조회 시 UsernameNotFoundException 발생")
        void shouldThrowUsernameNotFoundExceptionWhenNotFound() {
            String email = "nonexistent@test.com";

            when(memberPort.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage(email + " -> User not found.");
        }
    }
}
