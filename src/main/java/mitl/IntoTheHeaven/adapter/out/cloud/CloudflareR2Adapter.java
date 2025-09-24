package mitl.IntoTheHeaven.adapter.out.cloud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.CloudPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.time.Duration;

/**
 * Cloudflare R2 implementation of CloudPort using S3-compatible API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CloudflareR2Adapter implements CloudPort {

    @Value("${cloudflare.r2.access-key-id}")
    private String accessKeyId;

    @Value("${cloudflare.r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${cloudflare.r2.endpoint:}")
    private String endpointUrl;

    @Value("${cloudflare.r2.public-domain:}")
    private String publicDomain;

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    @PostConstruct
    public void initialize() {
        try {
            // Validate required configuration
            if (accessKeyId == null || accessKeyId.trim().isEmpty()) {
                throw new IllegalArgumentException("Cloudflare R2 access key ID is required");
            }
            if (secretAccessKey == null || secretAccessKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Cloudflare R2 secret access key is required");
            }
            if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
                log.warn("Cloudflare R2 endpoint not configured - using dummy endpoint for testing");
                endpointUrl = "https://dummy.r2.cloudflarestorage.com";
            }
            if (publicDomain == null || publicDomain.trim().isEmpty()) {
                log.warn("Cloudflare R2 public domain not configured - using dummy domain for testing");
                publicDomain = "pub-dummy.r2.dev";
            }
            // Build credentials
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

            // Build S3 client for R2
            s3Client = S3Client.builder()
                    .credentialsProvider(credentialsProvider)
                    .endpointOverride(URI.create(endpointUrl))
                    .region(Region.of("auto")) // R2 auto region selection
                    .forcePathStyle(true) // Required for R2
                    .build();

            // Build S3 presigner for R2
            s3Presigner = S3Presigner.builder()
                    .credentialsProvider(credentialsProvider)
                    .endpointOverride(URI.create(endpointUrl))
                    .region(Region.of("auto")) // R2 auto region selection
                    .build();

            log.info("Cloudflare R2 adapter initialized successfully with endpoint: {}", endpointUrl);

        } catch (Exception e) {
            log.error("Failed to initialize Cloudflare R2 adapter", e);
            throw new RuntimeException("Failed to initialize Cloudflare R2 adapter", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
        log.info("Cloudflare R2 adapter cleaned up");
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String objectKey,
            String contentType, Long contentLength, Duration expiration) {
        try {
            // Create PutObject request with content type only (no size constraints)
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    // No contentLength constraint - FE will resize and upload unknown sizes
                    .build();

            // Create presigned request
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .putObjectRequest(putRequest)
                    .build();

            // Generate presigned URL
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.debug("Generated presigned upload URL for bucket: {}, key: {}, expiration: {} minutes",
                    bucketName, objectKey, expiration.toMinutes());

            return presignedUrl;

        } catch (SdkException e) {
            log.error("Failed to generate presigned upload URL for bucket: {}, key: {}",
                    bucketName, objectKey, e);
            throw new RuntimeException("Failed to generate presigned upload URL: " + e.getMessage(), e);
        }
    }

    @Override
    public String generatePublicUrl(String objectKey) {
        try {
            return String.format("%s/%s", publicDomain, objectKey);
        } catch (Exception e) {
            log.error("Failed to generate public URL for bucket: {}, key: {}", publicDomain, objectKey, e);
            throw new RuntimeException("Failed to generate public URL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String bucketName, String objectKey) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.headObject(headRequest);
            return true;

        } catch (NoSuchKeyException e) {
            log.debug("File does not exist: bucket={}, key={}", bucketName, objectKey);
            return false;
        } catch (SdkException e) {
            log.error("Failed to check file existence for bucket: {}, key: {}", bucketName, objectKey, e);
            throw new RuntimeException("Failed to check file existence: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("Successfully deleted file: bucket={}, key={}", bucketName, objectKey);

        } catch (SdkException e) {
            log.error("Failed to delete file from bucket: {}, key: {}", bucketName, objectKey, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
