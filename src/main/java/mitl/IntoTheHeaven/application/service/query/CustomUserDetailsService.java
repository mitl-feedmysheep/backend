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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberPort.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> User not found."));
    }

    private UserDetails createUserDetails(Member member) {
        // 우선 모든 유저에게 "USER" 권한을 부여합니다.
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        return new User(
                String.valueOf(member.getId().getValue()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
} 