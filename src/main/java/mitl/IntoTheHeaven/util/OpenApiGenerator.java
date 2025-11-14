package mitl.IntoTheHeaven.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 서버 시작 시 OpenAPI 스펙을 자동으로 생성하는 컴포넌트
 * local 프로파일에서만 실행되며, openapi.json 파일이 없거나 강제 재생성 플래그가 있을 때만 실행됩니다.
 */
@Slf4j
@Component
public class OpenApiGenerator {

    private final ObjectMapper objectMapper;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Value("${openapi.generate-on-startup:true}")
    private boolean generateOnStartup;

    @Value("${openapi.force-regenerate:false}")
    private boolean forceRegenerate;

    public OpenApiGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // local 프로파일이 아니거나 생성 비활성화 시 스킵
        if (!activeProfiles.contains("local") || !generateOnStartup) {
            log.debug("OpenAPI 스펙 생성 스킵 (프로파일: {}, 생성 활성화: {})", activeProfiles, generateOnStartup);
            return;
        }

        String openApiDir = System.getProperty("user.dir") + "/../openapi";
        String openApiJsonPath = openApiDir + "/openapi.json";
        File openApiJsonFile = new File(openApiJsonPath);

        // 파일이 이미 있고 강제 재생성이 아니면 스킵
        if (openApiJsonFile.exists() && !forceRegenerate) {
            log.info("OpenAPI 스펙 파일이 이미 존재합니다: {}", openApiJsonPath);
            return;
        }

        // 비동기로 실행하여 서버 시작을 막지 않음
        new Thread(() -> {
            try {
                // 서버가 완전히 준비될 때까지 대기 (최대 10초)
                waitForServerReady(serverPort, 10000);
                
                log.info("📝 OpenAPI 스펙 생성 시작...");

                // 서버가 완전히 시작된 후 /v3/api-docs 엔드포인트에서 스펙 가져오기
                String apiDocsUrl = "http://localhost:" + serverPort + "/v3/api-docs";
                String openApiJson = fetchOpenApiSpec(apiDocsUrl);

                // 파일로 저장
                saveToFile(openApiJson, openApiJsonPath);

                log.info("✅ OpenAPI 스펙 생성 완료: {}", openApiJsonPath);

                // TypeScript 타입 생성
                generateTypeScriptTypes(openApiDir, openApiJsonPath);

            } catch (Exception e) {
                log.warn("⚠️ OpenAPI 스펙 생성 실패 (계속 진행): {}", e.getMessage());
                // 실패해도 서버는 계속 실행되도록
            }
        }).start();
    }
 
    private String fetchOpenApiSpec(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP 요청 실패: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine()).append("\n");
            }
        }

        // JSON 포맷팅 (이미 포맷되어 있을 수도 있지만, 확실하게)
        try {
            Object json = objectMapper.readValue(response.toString(), Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            // 포맷팅 실패 시 원본 반환
            return response.toString();
        }
    }

    private void saveToFile(String content, String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private void generateTypeScriptTypes(String openApiDir, String openApiJsonPath) {
        try {
            log.info("🔨 TypeScript 타입 생성 시작...");

            String outputPath = openApiDir + "/types.ts";
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "npx", "openapi-typescript",
                    openApiJsonPath,
                    "-o", outputPath);

            processBuilder.directory(new File(openApiDir));
            
            // 에러 출력도 확인할 수 있도록 설정
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();

            // 비동기로 실행 (서버 시작을 막지 않음)
            new Thread(() -> {
                try {
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        log.info("✅ TypeScript 타입 생성 완료: {}", outputPath);
                    } else {
                        log.warn("⚠️ TypeScript 타입 생성 실패 (exit code: {})", exitCode);
                        // 에러 출력 로그
                        try (Scanner scanner = new Scanner(process.getInputStream(), StandardCharsets.UTF_8)) {
                            while (scanner.hasNextLine()) {
                                log.warn("  {}", scanner.nextLine());
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.warn("⚠️ TypeScript 타입 생성 중단됨");
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            log.warn("⚠️ TypeScript 타입 생성 실패: {}", e.getMessage());
        }
    }

    private void waitForServerReady(int port, long timeoutMs) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                URL url = new URL("http://localhost:" + port + "/v3/api-docs");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return; // 서버 준비 완료
                }
            } catch (IOException e) {
                // 서버가 아직 준비되지 않음, 계속 대기
            }
            Thread.sleep(500); // 0.5초마다 체크
        }
        throw new InterruptedException("서버가 " + timeoutMs + "ms 내에 준비되지 않았습니다");
    }
}
