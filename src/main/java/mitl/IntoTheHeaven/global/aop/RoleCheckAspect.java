package mitl.IntoTheHeaven.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;
import mitl.IntoTheHeaven.application.service.query.ChurchQueryService;
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

    private final ChurchQueryService churchQueryService;

    @Before("@annotation(requireChurchRole)")
    public void checkRole(JoinPoint joinPoint, RequireChurchRole requireChurchRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("Invalid authentication context");
        }

        String userId = (String) jwtAuth.getPrincipal();
        String churchId = jwtAuth.getChurchId();

        if (churchId == null || churchId.isBlank()) {
            throw new AccessDeniedException("Church context is required");
        }

        ChurchRole currentRole = churchQueryService.getCurrentRole(
                MemberId.from(UUID.fromString(userId)),
                ChurchId.from(UUID.fromString(churchId)));

        if (currentRole == null || !currentRole.hasPermission(requireChurchRole.value())) {
            throw new AccessDeniedException(
                    "Required role: " + requireChurchRole.value() + ", Current role: " + currentRole);
        }
    }
}
