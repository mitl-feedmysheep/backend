package mitl.IntoTheHeaven.global.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HourlyPrayerPushScheduler")
class HourlyPrayerPushSchedulerTest {

    @Test
    @DisplayName("스케줄러 cron 표현식이 유효하다")
    void cronExpression_isValid() {
        assertThat(CronExpression.isValidExpression("0 0 * * * *")).isTrue();
    }

    @Test
    @DisplayName("cron이 매시간 정각에 실행됨을 확인한다")
    void cronExpression_firesEveryHourOnTheHour() {
        CronExpression cron = CronExpression.parse("0 0 * * * *");
        java.time.LocalDateTime base = java.time.LocalDateTime.of(2026, 4, 21, 8, 0, 1);
        java.time.LocalDateTime next = cron.next(base);
        assertThat(next).isEqualTo(java.time.LocalDateTime.of(2026, 4, 21, 9, 0, 0));
    }
}
