package mitl.IntoTheHeaven.application.service.query;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberPort memberPort;

    /**
     * Loads user details by email for Spring Security authentication.
     * This method is only used during login authentication process.
     *
     * @param email The user's email address
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberPort.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> User not found."));
    }

    private UserDetails createUserDetails(Member member) {
        // Grant "USER" authority to all users for now
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        return new User(
                String.valueOf(member.getId().getValue()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
} 