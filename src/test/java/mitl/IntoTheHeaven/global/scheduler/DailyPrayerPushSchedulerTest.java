package mitl.IntoTheHeaven.global.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DailyPrayerPushScheduler")
class DailyPrayerPushSchedulerTest {

    private static final String CRON = "0 0 9 * * *";

    @Test
    @DisplayName("스케줄러 cron 표현식이 유효하다")
    void cronExpression_isValid() {
        assertThat(CronExpression.isValidExpression(CRON)).isTrue();
    }

    @Test
    @DisplayName("당일 9시 이전이면 당일 9시에 실행된다")
    void cronExpression_firesTodayAt9amWhenBeforeIt() {
        CronExpression cron = CronExpression.parse(CRON);
        LocalDateTime base = LocalDateTime.of(2026, 4, 21, 8, 0, 1);
        LocalDateTime next = cron.next(base);
        assertThat(next).isEqualTo(LocalDateTime.of(2026, 4, 21, 9, 0, 0));
    }

    @Test
    @DisplayName("당일 9시가 지났으면 다음 실행은 다음날 9시이다 (당일 재발송 없음)")
    void cronExpression_doesNotRefireSameDayAfter9am() {
        CronExpression cron = CronExpression.parse(CRON);
        LocalDateTime base = LocalDateTime.of(2026, 4, 21, 9, 59, 56);
        LocalDateTime next = cron.next(base);
        assertThat(next).isEqualTo(LocalDateTime.of(2026, 4, 22, 9, 0, 0));
    }
}
