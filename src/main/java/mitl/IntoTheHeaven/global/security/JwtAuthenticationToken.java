package mitl.IntoTheHeaven.global.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String churchId;

    public JwtAuthenticationToken(Object principal, Object credentials,
                                  Collection<? extends GrantedAuthority> authorities,
                                  String churchId) {
        super(principal, credentials, authorities);
        this.churchId = churchId;
    }

    public String getChurchId() {
        return churchId;
    }
}


