package mitl.IntoTheHeaven.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;
import mitl.IntoTheHeaven.application.service.query.ChurchMemberService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final ChurchMemberService churchMemberService;

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("Invalid authentication context");
        }

        String userId = (String) jwtAuth.getPrincipal();
        String churchId = jwtAuth.getChurchId();

        ChurchRole currentRole = churchMemberService.getCurrentRole(
                MemberId.from(UUID.fromString(userId)),
                ChurchId.from(UUID.fromString(churchId))
        );

        if (currentRole == null || !currentRole.hasPermission(requireRole.value())) {
            throw new AccessDeniedException("Required role: " + requireRole.value() + ", Current role: " + currentRole);
        }
    }
}


