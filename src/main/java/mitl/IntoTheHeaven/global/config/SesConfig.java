package mitl.IntoTheHeaven.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * Configures AWS SES v2 client.
 * Region is provided via application properties.
 * Credentials resolution:
 *  - If aws.access-key-id and aws.secret-access-key are provided (non-blank), use them via StaticCredentialsProvider
 *  - Otherwise, fall back to DefaultCredentialsProvider (env vars, system props, ~/.aws, IAM role, etc.)
 */
@Configuration
public class SesConfig {

    @Value("${aws.ses.region}")
    private String awsSesRegion;

    @Value("${aws.access-key-id:}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:}")
    private String secretAccessKey;

    @Bean
    public SesV2Client sesV2Client() {
        Region region = Region.of(awsSesRegion);
        AwsCredentialsProvider credentialsProvider = resolveCredentialsProvider();
        return SesV2Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    private AwsCredentialsProvider resolveCredentialsProvider() {
        if (accessKeyId != null && !accessKeyId.isBlank() && secretAccessKey != null && !secretAccessKey.isBlank()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        }
        return DefaultCredentialsProvider.create();
    }
}


