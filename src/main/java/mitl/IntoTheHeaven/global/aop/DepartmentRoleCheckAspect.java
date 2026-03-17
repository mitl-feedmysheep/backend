package mitl.IntoTheHeaven.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.DepartmentId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;
import mitl.IntoTheHeaven.application.service.query.ChurchQueryService;
import mitl.IntoTheHeaven.application.service.query.DepartmentQueryService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Parameter;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DepartmentRoleCheckAspect {

    private final DepartmentQueryService departmentQueryService;
    private final ChurchQueryService churchQueryService;

    @Before("@annotation(requireDepartmentRole)")
    public void checkRole(JoinPoint joinPoint, RequireDepartmentRole requireDepartmentRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new AccessDeniedException("Invalid authentication context");
        }

        String userId = (String) jwtAuth.getPrincipal();
        String churchId = jwtAuth.getChurchId();

        if (churchId == null || churchId.isBlank()) {
            throw new AccessDeniedException("Church context is required");
        }

        // church SUPER_ADMIN은 모든 부서 접근 가능
        ChurchRole churchRole = churchQueryService.getCurrentRole(
                MemberId.from(UUID.fromString(userId)),
                ChurchId.from(UUID.fromString(churchId)));

        if (churchRole == ChurchRole.SUPER_ADMIN) {
            return; // SUPER_ADMIN bypass
        }

        // departmentId를 path variable에서 추출
        UUID departmentId = extractDepartmentId(joinPoint);
        if (departmentId == null) {
            throw new AccessDeniedException("Department context is required");
        }

        DepartmentRole currentRole = departmentQueryService.getCurrentRole(
                MemberId.from(UUID.fromString(userId)),
                DepartmentId.from(departmentId));

        if (currentRole == null || !currentRole.hasPermissionOver(requireDepartmentRole.value())) {
            throw new AccessDeniedException(
                    "Required department role: " + requireDepartmentRole.value() + ", Current role: " + currentRole);
        }
    }

    private UUID extractDepartmentId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            PathVariable pathVar = parameters[i].getAnnotation(PathVariable.class);
            if (pathVar != null && "departmentId".equals(pathVar.value())) {
                if (args[i] instanceof UUID uuid) {
                    return uuid;
                }
                return UUID.fromString(args[i].toString());
            }
            // Also check parameter name
            if ("departmentId".equals(parameters[i].getName()) && args[i] instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }
}
