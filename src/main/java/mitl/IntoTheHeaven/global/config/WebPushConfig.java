package mitl.IntoTheHeaven.global.config;

import lombok.Getter;
import lombok.Setter;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
@ConfigurationProperties(prefix = "webpush.vapid")
@Getter
@Setter
public class WebPushConfig {

    private String publicKey;
    private String privateKey;
    private String subject;

    @Bean
    public PushService pushService() throws Exception {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        return new PushService(publicKey, privateKey, subject);
    }
}
