package mitl.IntoTheHeaven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IntoTheHeavenApplication {

  public static void main(String[] args) {
    SpringApplication.run(IntoTheHeavenApplication.class, args);
  }
}
