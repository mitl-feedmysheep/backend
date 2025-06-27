package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.application.port.in.command.LoginUseCase;
import mitl.IntoTheHeaven.global.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. The `AuthenticationManager` attempts to authenticate using the `UsernamePasswordAuthenticationToken`.
        // 2. Internally, the `DaoAuthenticationProvider` is activated.
        // 3. The `DaoAuthenticationProvider` calls `CustomUserDetailsService` to retrieve user information from the DB via `loadUserByUsername`.
        //    - If the user is not found, a `UsernameNotFoundException` is thrown here.
        // 4. Once the `UserDetails` is returned, the `DaoAuthenticationProvider` uses the `PasswordEncoder` to compare the submitted password with the encrypted password from the DB.
        //    - If the passwords do not match, a `BadCredentialsException` is thrown here.
        // 5. If all authentication processes are successful, an `Authentication` object containing the user's information is returned.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
} 