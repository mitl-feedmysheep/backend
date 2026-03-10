package mitl.IntoTheHeaven.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter KST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private static final Set<String> BODY_METHODS = Set.of("POST", "PUT", "PATCH");
    private static final int MAX_BODY_LOG_LENGTH = 1000;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/health"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        return EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String timestamp = ZonedDateTime.now(KST).format(KST_FORMATTER);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(wrappedRequest, wrappedResponse, timestamp, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String timestamp,
            long duration
    ) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        int status = response.getStatus();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s %s", timestamp, method, uri));

        if (queryString != null) {
            sb.append("?").append(queryString);
        }

        sb.append(String.format(" → %d (%dms)", status, duration));

        if (BODY_METHODS.contains(method)) {
            String body = getBody(request);
            if (!body.isEmpty()) {
                sb.append("\n  Body: ").append(body);
            }
        }

        if (status >= 400) {
            log.warn(sb.toString());
        } else {
            log.info(sb.toString());
        }
    }

    private String getBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        String body = new String(content, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
        if (body.length() > MAX_BODY_LOG_LENGTH) {
            return body.substring(0, MAX_BODY_LOG_LENGTH) + "...(truncated)";
        }
        return body;
    }
}
