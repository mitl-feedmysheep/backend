package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.application.port.in.command.AuthCommandUseCase;
import mitl.IntoTheHeaven.global.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService implements AuthCommandUseCase {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final ChurchPort churchPort;
        private final MemberPort memberPort;

        @Override
        public LoginResponse login(LoginRequest request) {

                // 1. The `AuthenticationManager` attempts to authenticate using the
                // `UsernamePasswordAuthenticationToken`.
                // 2. Internally, the `DaoAuthenticationProvider` is activated.
                // 3. The `DaoAuthenticationProvider` calls `CustomUserDetailsService` to
                // retrieve user information from the DB via `loadUserByUsername`.
                // - If the user is not found, a `UsernameNotFoundException` is thrown here.
                // 4. Once the `UserDetails` is returned, the `DaoAuthenticationProvider` uses
                // the `PasswordEncoder` to compare the submitted password with the encrypted
                // password from the DB.
                // - If the passwords do not match, a `BadCredentialsException` is thrown here.
                // 5. If all authentication processes are successful, an `Authentication` object
                // containing the user's information is returned.
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                String accessToken = jwtTokenProvider.createAccessToken(authentication);

                Member member = memberPort.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                return LoginResponse.builder()
                                .accessToken(accessToken)
                                .isProvisioned(member.getIsProvisioned())
                                .build();
        }

        @Override
        public LoginResponse adminLogin(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                // Issue a token without churchId first (GLOBAL-like)
                String accessToken = jwtTokenProvider.createAccessToken(authentication);

                Member member = memberPort.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("Member not found"));

                return LoginResponse.builder().accessToken(accessToken).isProvisioned(member.getIsProvisioned())
                                .build();
        }

        @Override
        public LoginResponse selectChurch(MemberId memberId, ChurchId churchId) {
                ChurchMember cm = churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId);
                if (cm == null || !cm.getRole().hasPermissionOver(ChurchRole.LEADER)) {
                        throw new RuntimeException("Not an admin of the selected church");
                }
                Authentication auth = new UsernamePasswordAuthenticationToken(
                                memberId.getValue().toString(),
                                "",
                                List.of(new SimpleGrantedAuthority("USER")));
                String token = jwtTokenProvider.createAccessToken(auth, churchId.getValue().toString());
                return LoginResponse.builder().accessToken(token).build();
        }
}